package net.b07z.sepia.sdk.services.uid1003;

import java.util.TreeSet;

import org.json.simple.JSONObject;

// import net.b07z.sepia.sdk.services.uid1003.GetFirstNumber;
import net.b07z.sepia.server.assist.answers.ServiceAnswers;
import net.b07z.sepia.server.assist.assistant.LANGUAGES;
import net.b07z.sepia.server.assist.data.Parameter;
import net.b07z.sepia.server.assist.interpreters.NluResult;
import net.b07z.sepia.server.assist.interpreters.NluTools;
import net.b07z.sepia.server.assist.interpreters.Normalizer;
import net.b07z.sepia.server.assist.interpreters.NormalizerLight;
import net.b07z.sepia.server.assist.interviews.InterviewData;
import net.b07z.sepia.server.assist.parameters.CustomParameter;
import net.b07z.sepia.server.assist.services.ServiceAccessManager;
import net.b07z.sepia.server.assist.services.ServiceBuilder;
import net.b07z.sepia.server.assist.services.ServiceInfo;
import net.b07z.sepia.server.assist.services.ServiceInterface;
import net.b07z.sepia.server.assist.services.ServiceResult;
import net.b07z.sepia.server.assist.tools.DateTimeConverters;
import net.b07z.sepia.server.assist.users.ACCOUNT;
import net.b07z.sepia.server.assist.services.ServiceInfo.Content;
import net.b07z.sepia.server.assist.services.ServiceInfo.Type;
import net.b07z.sepia.server.core.assistant.PARAMETERS;
import net.b07z.sepia.server.core.data.Answer;
import net.b07z.sepia.server.core.data.Language;
import net.b07z.sepia.server.core.tools.Debugger;
import net.b07z.sepia.server.core.tools.JSON;
import net.b07z.sepia.server.core.tools.Sdk;
import net.b07z.sepia.server.assist.data.Card;
import net.b07z.sepia.server.assist.data.Card.ElementType;
import net.b07z.sepia.server.core.assistant.ACTIONS;
import net.b07z.sepia.server.core.tools.JSON;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.math.*;

/**
 * Demo for a restaurant reservation service.
 * 
 * @author Florian Quirin (RestrauntDemo - this file's template)
 * @author 42null		  (BasicMath - this file)
 * 
 * @version #0.02.02
 * 
 */
public class BasicMath implements ServiceInterface {
	
	//Command name of your service (will be combined with userId to be unique)
	private static final String CMD_NAME = "basic_math";

// -------------------- #AREA FOR SETTING'S VARAIBLES --------------------
private static final boolean makePublic = false;
private static final String[] wakeWords = {"whatis","caulacate"};
// -----------------------------------------------------------------------

	//Define some sentences for testing:
	
	@Override
	public TreeSet<String> getSampleSentences(String lang) {
		TreeSet<String> samples = new TreeSet<>();
		//GERMAN
		if (lang.equals(Language.DE.toValue())){
		//OTHER
		}else{
			samples.add("Whatis help");
			samples.add("Whatis 5.43331 times (nineteen / forty seven point nine 7)");
		}
		return samples;
	}
	
	//Basic service setup:
	
	//Overriding the 'getAnswersPool' methods enables you to define custom answers with more complex features.
	//You can build a pool of answers that can have multiple versions of the same answer used for different 
	//situations like a repeated question (what was the time? -> sorry say again, what was the time? -> ...).

	@Override
	public ServiceAnswers getAnswersPool(String language) {
		ServiceAnswers answerPool = new ServiceAnswers(language);
		
		//Build English answers
		if (language.equals(LANGUAGES.EN)){
			answerPool
				.addAnswer(successAnswer, 0, "Ok, that would be <1>")
								//example of how to use the 'shortcut' to add an answer
				
				.addAnswer(okAnswer, 0, "Sorry there has been an error @okAnswer")
				.addAnswer(askFirstNumber, 0, "Sorry, it appears that you did not enter any operators, could you please add some?")
						// Answer.Character.neutral, 2, 5))
				.addAnswer(new Answer(Language.from(language), askFirstNumber, "Sorry I did not understand your first number,"
								+ "Could you tell me once more please?", 
				Answer.Character.neutral, 2, 5))
						//example of how to use the 'complete' answer object
				;
			return answerPool;
		
		//Other languages are not used in this demo yet
		}else{
			return null;
		}
	}
	//We keep a reference here for easy access in getResult - Note that custom answers need to start with a certain prefix
	private static final String failAnswer = "error_0a";
	private static final String successAnswer = ServiceAnswers.ANS_PREFIX + "restaurant_success_0a";
	private static final String okAnswer = ServiceAnswers.ANS_PREFIX + "restaurant_still_ok_0a";

	private static final String askFirstNumber = ServiceAnswers.ANS_PREFIX + "simpleMath_ask_first_Number_1a";

	@Override
	public ServiceInfo getInfo(String language) {
		//Type of service (for descriptions, choose what you think fits best)
		ServiceInfo info = new ServiceInfo(Type.plain, Content.data, false);
		
		//Should be available publicly or only for the developer? Set this when you are done with testing and want to release
		if(makePublic){
			info.makePublic();
		}
		//Command
		info.setIntendedCommand(Sdk.getMyCommandName(this, CMD_NAME));
		
		//Direct-match trigger sentences in different languages:
		String EN = Language.EN.toValue();
		/*info.setCustomTriggerRegX(".*\\b("
					+ "(whatis|caulacate)\\b.* (+|-|/|^)"
				+ ")\\b.*", EN);*/

		String tempString = "";
		for(int i = 0; i < wakeWords.length; i++){
			tempString += wakeWords[i]+"|";
		}
		tempString = tempString.substring(0,tempString.length()-2);
		info.setCustomTriggerRegX(tempString,EN);

		info.setCustomTriggerRegXscoreBoost(5);		//boost service a bit to increase priority over similar ones
		
		//Parameters:
		
		//This service has a number of required parameters (without them we can't complete the reservation).
		//Required parameters will be asked automatically by SEPIA using the defined question.
		
		Parameter p1 = new Parameter(new GetFirstNumber())
				.setRequired(true)
				.setQuestion(askFirstNumber);
		
		info.addParameter(p1);//.addParameter(p2).addParameter(p3);
		
		//Answers (these are the default answers, you can trigger a custom answer at any point in the module 
		//with serviceBuilder.setCustomAnswer(..)):
		info.addSuccessAnswer(successAnswer)
			.addFailAnswer(failAnswer)
			.addOkayAnswer(okAnswer);
			//.addCustomAnswer("askTimeAndDate", askTimeAndDate); 	//optional, just for info
		
		//Add answer parameters that are used to replace <1>, <2>, ... in your answers.
		//The name is arbitrary but you need to use the same one in getResult(...) later for api.resultInfoPut(...)
		info.addAnswerParameters("firstNumber");//, "number", "name"); 	//<1>=time, <2>=number, ...
		
		return info;
	}
	
	@Override
	public ServiceResult getResult(NluResult nluResult) {
		//initialize result
		ServiceBuilder api = new ServiceBuilder(nluResult, 
				getInfoFreshOrCache(nluResult.input, this.getClass().getCanonicalName()),
				getAnswersPool(nluResult.language));
		
		//get required parameters:
		
		//-name - NOTE: custom parameter has different naming
		Parameter nameParameter = nluResult.getRequiredParameter(GetFirstNumber.class.getName());
		String firstNumber = nameParameter.getValueAsString();
		
		//get optional parameters
		//NONE
		
		//Set answer parameters as defined in getInfo():
		api.resultInfoPut("firstNumber", firstNumber);
		
		//This service basically cannot fail ... ;-)
		// ... here you would call your reservation method/API/program ...

		// /*boolean wasSent =*/ service.sendFollowUpMessage(nluResult.input, service.buildResult());
		//Just for demo purposes we add a button-action with a link to the SDK

		// if(pageToReturn == 1){
		// }

		if((firstNumber).contains("--{:Help Page:}--")){
			api.addAction(ACTIONS.BUTTON_IN_APP_BROWSER);
			api.putActionInfo("url", "https://github.com/42null/42null-SEPIA-Extensions");
			api.putActionInfo("title", "Source code");
			
			//... and we also add a demo card
			Card card = new Card(Card.TYPE_SINGLE);
			JSONObject linkCard = card.addElement(
					ElementType.link, 
					JSON.make("title", "Basic Math Documentation" + ":", "desc", "By 42null"),
					null, null, "", 
					"https://github.com/42null/42null-SEPIA-Extensions/blob/master/README.md", 
					"https://sepia-framework.github.io/img/icon.png", 
					null, null
			);
			JSON.put(linkCard, "imageBackground", "#000");	//more options like CSS background
			api.addCard(card.getJSON());
		}else if((firstNumber).contains("--pi")){
			
		}

		//all good
		api.setStatusSuccess();
		
		//build the API_Result
		ServiceResult result = api.buildResult();
		return result;
	}
	
	//----------------- custom parameters -------------------
	
	/**
	 * Parameter handler that tries to extract a reservation name.
	 */
	public static class GetFirstNumber extends CustomParameter {
		boolean debugMode = false; //TODO: Include a operator in input, also, should their be a int and then we could have difrent modes? 
		String debugStr = "";
		char dontUseMeChar = 'j';//TODO: Add a detection to check if this character is used
		String dontUseMeStr = "j";

// Moved to be more global
		ArrayList<BigDecimal> numberArray = new ArrayList<BigDecimal>();
		ArrayList<Integer> operatorPlace = new ArrayList<Integer>();
		ArrayList<Integer> itemArrayMeta = new ArrayList<Integer>();//OPTION: Use a 2D array instead?
		BigDecimal caulactedNumber = new BigDecimal("0");
		// DEBUG
		String str7 = "";
		// Extra options + disclaimers
		int pageToReturn; // 1= help page, 
		boolean disclaimerPi = false;
		boolean disclaimerFactorial = false;

		@Override
		public String extract(String input) {
			boolean dontReturn = false;
			String extracted = removeUnwanted(input+" ");// ' ' required to add ending seperation
			if(extracted.equals("")){
				// if(true){
				return "";
			}else if(extracted.equals("--help")){
				pageToReturn = 1;
				return returnHelp();
			}else if(extracted.contains("π")){//"--pi")){
				// extracted.replaceAll("--pi","");//TODO: To make it more effishant should I just do substring?
				// extracted = extracted.substring(0,extracted.length()-3);//Remove "--pi"
				disclaimerPi = true;
			}else if(extracted.contains("!")){
				disclaimerFactorial = true;
			}
			// extracted = extracted.replace(dontUseMeChar,' ');//TODO: Fix this
			extracted = extracted.replaceAll(dontUseMeStr,"");
			

			//English

			if (this.language.equals(LANGUAGES.EN)){
				// debugStr = extracted;

				char currentChar_;
				String currentItem_ = "";
				int currentItemMeta_;
				boolean canAdd = false;
				BigDecimal intermideate_;
				Boolean whileRan_ = false;
				extracted = "(("+extracted+")"+dontUseMeStr;
				extracted = extracted.replaceAll(" ","");
				// if(true){return extracted;}
// ItemArayMeta: 0 = unknown/error, 1 = number, 2 = operator, 2 = wrapper ( '(' or ')' )
				boolean notANumber = true;

				for(int i = 0; i < extracted.length()-1; i++){
					currentChar_ = extracted.charAt(i);

					// if(i = extracted.length){

					// }
					if(isCharNumber(currentChar_) || currentChar_ == '.' || currentChar_ == '−'){
						notANumber = false;
						// UNESSERY - spaceless, should have done it sooner
						/*while(extracted.charAt(i+1) == ' ' && isCharNumber(extracted.charAt(i+2)) && i < (extracted.length() - 1)){
							// extracted = (extracted.substring(0,i)) + (extracted.substring(i+1,(extracted.length())-1));
							// extracted = (extracted.substring(0,i+1)) + (extracted.substring(i+2,(extracted.length())-1));
							extracted = (extracted.substring(0,i+1)) + (extracted.substring(i+2,(extracted.length())-1));
							// if(true){return "_"+extracted+"_";}
							// currentChar_ = extracted.charAt(i);
							// return "_"+currentChar_+"_";
							currentItem_ += currentChar_;
							// if(true){ return "extracted = _"+extracted+"_ i = "+i+" currentChar_ = "+currentChar_; }
							
							i++;
							counter++;
							currentChar_ = extracted.charAt(i);

							whileRan_ = true;
							// return "extracted = _"+extracted+ "i = "+i+" _"+((extracted.charAt(i+1)) == ' ' && isCharNumber(extracted.charAt(i+2)));
						}*/
						
						if(currentChar_ == '−'){
							currentChar_ = '-';
						}else{
							currentChar_ = extracted.charAt(i);
						}

						if(!whileRan_){
							currentItem_ += currentChar_;
						}else{
							whileRan_ = false;
						}

						whileRan_ = false;


						if(!(isCharNumber(extracted.charAt(i+1)) || (extracted.charAt(i+1)) == '.')){
							intermideate_ = new BigDecimal(Double.parseDouble(currentItem_)+"");
							numberArray.add(intermideate_);
							itemArrayMeta.add(1); //Number
							currentItem_ = "";
						}


// operatorArray: 0 = unknown/error, + = 1, - = 2, ✕ = 3, ÷ = 4
					}else if(currentChar_ == '+'){
						itemArrayMeta.add(2); //Operator +
					}else if(currentChar_ == '-'){
						itemArrayMeta.add(3); //Operator -
					}else if(currentChar_ == '✕'){
						itemArrayMeta.add(4); //Operator ✕
					}else if(currentChar_ == '÷'){
						itemArrayMeta.add(5); //Operator ÷
					}else if(currentChar_ == '^'){
						itemArrayMeta.add(6); //Operator ^
					}else if(currentChar_ == '('){
						itemArrayMeta.add(7); //Operator (
					}else if(currentChar_ == ')'){
						itemArrayMeta.add(8); //Operator )
					}else if(currentChar_ == '!'){
						itemArrayMeta.add(9); //Operator ! (factorial)
					}else if(currentChar_ == 'π'){	//Sepcial number case - pi
						notANumber = false;
						itemArrayMeta.add(1); //Number
						numberArray.add(new BigDecimal(Double.parseDouble("3.14159265358979323846")));
						currentItem_ = "";
					}

					if(notANumber){
						// intermideate_ = new BigDecimal(Double.parseDouble(currentItem_)+"");
						numberArray.add(null);
					}else{
						notANumber = true;
					}

				}
				extracted = removePlace(extracted,-1,true); //extracted.length()-1); //-1 for last space
				// extracted = extracted.substring(0,extracted.length()-1);

	
				if(numberArray.size() == 0){
					return "";
				}
				// debugStr = itemArrayMeta+"";
// ItemArayMeta: 0 = unknown/error, 1 = number, 2 = operator (includes parenthesies),
// operatorArray: 0 = unknown/error, + = 1, - = 2, ✕ = 3, ÷ = 4, ^ = 5

					// debugDouble = 0.0;
					// BigDecimal presentNumber_ = new BigDecimal("0");//BigDecimal.ZERO);
					// // double currentNum_ = 0;
					// BigDecimal currentNum_;
					// int currentOperator_ = -1;
					// int currentMeta_;
					// int lastMeta_ = -1;
					// int numArrayPlace_ = 0;
					// int operatorArrayPlace_ = 0;

					// MathContext mc_ = new MathContext(18);//longestValueLength(numberArray)-1);

					addInMultiplication();
					// fixOutOfPlacePowers(); //TODO: Fix
					removeSingularSections();
					orderOfOperations();
					str7 = itemArrayMeta+"";

					while(itemArrayMeta.contains(2)||itemArrayMeta.contains(3)||itemArrayMeta.contains(4)||itemArrayMeta.contains(5)||itemArrayMeta.contains(6)){
						caulactedNumber = splitAndConquer();
						// removeSingularSections();
					}

				// check some specials and access account if allowed
				// if (extracted.equals("my")){
				// 	//access account
				// 	ServiceAccessManager sam = new ServiceAccessManager("demoKey");
				// 	if (sam.isAllowedToAccess(ACCOUNT.USER_NAME_LAST)){
				// 		extracted = nluInput.user.getName(sam);
				// 	}else{
				// 		//refuse and ask again - a real service should handle this with a more specific follow-up question
				// 		extracted = "<user_data_unresolved>"; 
				// 	}
				// }
			
			//Other languages
			}else{
				Debugger.println("Custom parameter 'firstNumber' does not support language: " + this.language, 1);
			}
			
			//Reconstruct original text format (before normalization) - This is just a cosmetic change
			// if (!extracted.isEmpty()){
			// 	Normalizer normalizer = new NormalizerLight();
			// 	extracted = normalizer.reconstructPhrase(nluInput.textRaw, extracted);
			// }

// Include?
			// String returnThis_ = "";
			// if(false){ returnThis_ += "["+extracted+"] ";}
			// if(true){  returnThis_ += "itemArrayMeta: ["+itemArrayMeta+"] ";}
			// if(false){ returnThis_ += "numberArray: ["+numberArray+"] ";}
			// if(true){  returnThis_ += "operatorArray: ["+operatorArray+"] ";}
			// if(true){  returnThis_ += "debugDouble: ["+debugDouble+"] ";}
			// if(false){ returnThis_ += "debugStr: ["+debugStr+"] ";}

// Remove .0 artificat from caulactedNumber
			String caulactedNumberStr = caulactedNumber+"";
			if((caulactedNumber.doubleValue() % 1) == 0 && !(caulactedNumberStr.contains("E+"))){
				caulactedNumberStr = caulactedNumberStr.substring(0,caulactedNumberStr.indexOf("."));
			}

			if(disclaimerPi){
				caulactedNumberStr +=", for π the approximation of '3.14159265358979312' was used.";
			}
			if(disclaimerFactorial){
				caulactedNumberStr += " Factorial (!) was used, please note that due to the structure of this program your answer may not be acurate.";
			}


			if(dontReturn){
				return "";
			}else if(debugMode){
				// return debugDouble+""+" debugStr =_"+debugStr+/*" operatorArray: "+operatorArray+" itemArrayMeta: "+itemArrayMeta+*/"_ numberArray: "+numberArray+/*" extracted: _"+extracted+*/"_ str7:"+str7;
				String returnThis = caulactedNumber+"_";
				returnThis += "numberArray: "+numberArray+"_";
				returnThis += "itemArrayMeta: "+itemArrayMeta+"_";
				returnThis += "str7: "+str7+"_";
				// returnThis += "numberPlace: "+numberPlace+"_";
				// returnThis += "extracted: "+extracted+"_";
				return returnThis;
			}else{
				return caulactedNumberStr+"";
			}
		}
		

		public void addInMultiplication(){
			for(int i = 1; i < itemArrayMeta.size()-1; i++){
				if((itemArrayMeta.get(i)==8 && itemArrayMeta.get(i+1)==1) || (itemArrayMeta.get(i)==1 && itemArrayMeta.get(i+1)==7)){
					itemArrayMeta.add(i+1, 4); // *
					numberArray.add(i+1,null);
					i = 1;//TODO: Can I make this more efficent?
				}
			}
		}

		// 1 6 1 
		// 2 n 3 

		public void fixOutOfPlacePowers(){
			for(int i = 1; i < itemArrayMeta.size()-1; i++){
				if(itemArrayMeta.get(i)==1 && itemArrayMeta.get(i+1)==1 && itemArrayMeta.get(i+2)==6){
					itemArrayMeta.remove(i+2);
					itemArrayMeta.add(i+1, 6); // ^
					numberArray.remove(i+2);
					numberArray.add(i+1,null);
					i = 1;//TODO: Can I make this more efficent?
				}
			}
		}

		public void removeSingularSections(){
			for(int i = 1; i < itemArrayMeta.size(); i++){
				if(itemArrayMeta.get(i)==1 && itemArrayMeta.get(i-1)==7 && itemArrayMeta.get(i+1)==8){
					itemArrayMeta.remove(i+2);
					itemArrayMeta.remove(i-1);
					numberArray.remove(i+2);
					numberArray.remove(i-1);
					i = 1;//TODO: Can I make this more efficent?
				}
			}
		}

		public void orderOfOperations(){
			itemArrayMeta.add(0,7);
			numberArray.add(0,null);
			itemArrayMeta.add(itemArrayMeta.size()-1,8);
			numberArray.add(numberArray.size()-1,null);

			int currentMeta_ = 0;
			boolean innerGo_ = true;
			int j;
			int sevenEightCount = 0;

			int operators_[] = {/*9,9,*/6,6,5,4};//,3,2};
			int currentOperatorNum_;
			for(int k = 0; k < operators_.length; k = k+2){
				currentOperatorNum_ = operators_[k];
				for(int i = 0; i < itemArrayMeta.size(); i++){
					currentMeta_ = itemArrayMeta.get(i);
					innerGo_ = true;
					if(currentMeta_ == currentOperatorNum_ || currentMeta_ == currentOperatorNum_-1){
						j = i+1;
						while(innerGo_){
							if(itemArrayMeta.get(j) == 7){
								sevenEightCount++;
							}else if((itemArrayMeta.get(j) == 8)){
									sevenEightCount--;
							}
							if(sevenEightCount == 0){
								itemArrayMeta.add(j+1,8);
								numberArray.add(j+1,null);
								innerGo_ = false;
							}
							j++;
						}
						j = i-1;
						innerGo_ = true;
						while(innerGo_){
							if(itemArrayMeta.get(j) == 8){
								sevenEightCount++;
							}else if((itemArrayMeta.get(j) == 7)){
									sevenEightCount--;
							}
							if(sevenEightCount == 0){
								itemArrayMeta.add(j,7);
								numberArray.add(j,null);
								innerGo_ = false;
							}
							j--;
						}
					i = i + 2;}
				}
			}
		}

		public BigDecimal splitAndConquer(/*ArrayList<Integer> arrayList_, int wanted_*/){
			int closestItemMetaA_ = -1;
			int closestItemMetaB_ = -1;
			ArrayList<Integer> itemArrayMeta_ = new ArrayList<Integer>();



			for(int i = 0; i < itemArrayMeta.size()+1; i++){
				if(itemArrayMeta.get(i) == 7){ // '('
					closestItemMetaA_ = i;
				}else if(itemArrayMeta.get(i) == 8){ // ')'
					closestItemMetaB_ = i+1;
					i = itemArrayMeta.size()+1;
				}
			}

			itemArrayMeta_ = new ArrayList<Integer>(itemArrayMeta.subList(closestItemMetaA_+1,closestItemMetaB_-1));
			
			ArrayList<Integer>itemArrayMetaTemp_ = new ArrayList<Integer>(itemArrayMeta);
			itemArrayMeta = new ArrayList<Integer>((itemArrayMetaTemp_.subList(0,closestItemMetaA_)));
			itemArrayMeta.addAll(itemArrayMetaTemp_.subList(closestItemMetaB_,itemArrayMetaTemp_.size()));


			ArrayList<BigDecimal> numberArray_ = new ArrayList<BigDecimal>(numberArray.subList(closestItemMetaA_+1,closestItemMetaB_-1));

			ArrayList<BigDecimal>numberArrayTemp_ = new ArrayList<BigDecimal>(numberArray);
			numberArray = new ArrayList<BigDecimal>((numberArrayTemp_.subList(0,closestItemMetaA_)));
			numberArray.addAll(numberArrayTemp_.subList(closestItemMetaB_-1,numberArrayTemp_.size()));

			return doMath(itemArrayMeta_, numberArray_, closestItemMetaA_);
		}

		public BigDecimal /*boolean*/ doMath(ArrayList<Integer> itemArrayMeta_, ArrayList<BigDecimal> numberArray_, int locationTakenFrom_){
			BigDecimal presentNumber_ = new BigDecimal("0");//BigDecimal.ZERO);
			BigDecimal currentNum_;
			int currentOperator_ = -1;
			int currentMeta_ =-1;
			int lastMeta_ = -1;
			// int numArrayPlace_ = 0;
			int operatorArrayPlace_ = 0;
			MathContext mc_ = new MathContext(18);//longestValueLength(numberArray)-1);

			for(int i = 0; i < itemArrayMeta_.size();i++){
				currentMeta_ = itemArrayMeta_.get(i);

				/*if(currentMeta_ == lastMeta_){
					// return "You cannot have two numbers in sequence without an operator";
		}else*/ if(currentMeta_ == 1){//If a number
					// if(currentNum_ = null){
					// 	return "You need an operator";}
					currentNum_ = numberArray_.get(i);
					// numArrayPlace_++;
					if(currentNum_ == null){
						// return "there was a problem";
					}

					if(i == 0){//TODO: Remove
						presentNumber_ = numberArray_.get(0);
					} else {
						if(lastMeta_ == 2 /*currentOperator_== 1*/){
							presentNumber_ = presentNumber_.add(currentNum_,mc_);
						}else if(lastMeta_ == 3){
							presentNumber_ = presentNumber_.subtract(currentNum_,mc_);
							// debugDouble -= currentNum_;
						}else if(lastMeta_ == 4){
							presentNumber_ = presentNumber_.multiply(currentNum_,mc_);
							// debugDouble *= currentNum_;
						}else if(lastMeta_ == 5){
							presentNumber_ = presentNumber_.divide(currentNum_,mc_);
							// debugDouble /= currentNum_;
						}else if(lastMeta_ == 6){
							presentNumber_ = presentNumber_.pow(currentNum_.intValue(),mc_);
						}else if(lastMeta_ == 9){ //factorial
							for(int j = currentNum_.intValue(); j > 0; j--){
								presentNumber_ = presentNumber_.multiply(new BigDecimal(j),mc_);
							}
						}
					}

				}else{//If an operator
				}
				lastMeta_ = currentMeta_;
			}
			// str7 += "_"+presentNumber_+"";

			// Add number back to arrayList
			// itemArrayMeta.add(locationTakenFrom_, 1);// 1 = number
			// itemArrayMeta.add(locationTakenFrom_, presentNumber_);
			// CONSIDERATION: Clean up to be in order?
			numberArray.remove(locationTakenFrom_-1);//Remove (
			itemArrayMeta.add(locationTakenFrom_,1);
			numberArray.add(locationTakenFrom_,presentNumber_);
			// numberPlace.add(locationTakenFrom_);

			return presentNumber_;
		}

		public static String returnHelp(){
			String returnThis_;
			returnThis_ =  "--{:Help Page:}-- ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ";// \n
			returnThis_ += "Simplemath is a sdk extenson that uses the 'Whatis' or 'caulacate' command to solve math problems. It can handle addition, subtraction, multiplication, division, whole powers, negitive numbers, pi, parenthesies, order of operations, spelled out numbers and more, this extension is still under construction.";// It uses no external API's";
			return returnThis_;
		}

		public String removePlace(String input_, int removeSpace_,boolean ignoreIfAbove_){
			/*
			//Documentation: Key table
			positive int = replace that place in the string -- NOT starting from 1
			value higher then aviable places then it will be ignored, code is abaiable to make it remove the last one if too high -- check ignoreIfAbove_
			-1 = last char in string
			*/

			if(removeSpace_ >= input_.length()){
				if(ignoreIfAbove_){ return input_;}
				else{removeSpace_ = -1;}//Take care if too high of a number given - removes the last
			}

			if(removeSpace_ == -1){
				removeSpace_ = input_.length()-1;
			}

			return (input_.substring(0,removeSpace_)) + (input_.substring(removeSpace_+1,(input_.length())));
		}

		public String removeUnwanted(String input_){
			input_ = input_.toLowerCase();

			if(input_.contains("help")){
				return "--help";
			}else if(input_.contains("debug")){
				debugMode = true;
				input_ = input_.replaceAll("debug","");
			}

// "Wake" words
			input_ = input_.replaceAll("\\b(whatis|caulacate)\\b", "");

// OPERATORS
			input_ = input_.replaceAll("multiplied", "✕");
			input_ = input_.replaceAll("times", "✕");
			input_ = input_.replaceAll("divided", "÷");
			input_ = input_.replaceAll("/", "÷");
			input_ = input_.replaceAll("\\b(plus|added)\\b", "+");
			input_ = input_.replaceAll("\\b(minus|subtracted|subtacts)\\b", "−");
			input_ = input_.replaceAll("pow", "^");

// SPECIAL CASES
			input_ = input_.replaceAll("neg","−");
			input_ = input_.replaceAll("pi","π");//3.14159265358979323846
			input_ = input_.replaceAll("squared","^2");
			input_ = input_.replaceAll("cubed","^3");
			input_ = input_.replaceAll("point", "z");// "∙"
			// TODO: Find a way to replaceAll point to ".", the . is haveing problems and I have spent way to long on it			

// FILLER/SPACER WORDS
			input_ = input_.replaceAll("by", "");
			input_ = input_.replaceAll("to", "");
			input_ = input_.replaceAll("of", "");
			input_ = input_.replaceAll("by", "");
			input_ = input_.replaceAll("the", "");
			input_ = input_.replaceAll("and", "");
			input_ = input_.replaceAll("itive", ""); //(For negitive)
			input_ = input_.replaceAll("er", ""); //(For power)


// String to num converter
			input_ = allToNum(input_);


			input_ = "  "+input_+"  ";
			String tempInputA_;
			String tempInputB_;
			while(input_.contains("z")){ //TODO: Because the length can change, should I modify "i" during exucution to improve effecencey?
				for(int i = 0; i < input_.length()-1; i++){
					if(input_.charAt(i) == 'z'){
						if(input_.charAt(i-1) == ' '){
							tempInputA_ = (input_.substring(0,i-1))+".";
						}else{
							tempInputA_ = (input_.substring(0,i))+".";
						}
						if(input_.charAt(i+1) == ' '){
							tempInputB_ = input_.substring(i+2,input_.length()-1);
						}else{
							tempInputB_ = input_.substring(i+1,input_.length()-1);
						}
						input_ = tempInputA_ + tempInputB_;
			}	}	}

			if(input_.indexOf("+")==-1&&input_.indexOf("-")==-1&&input_.indexOf("^")==-1&&input_.indexOf("✕")==-1&&input_.indexOf("÷")==-1&&input_.indexOf("(")==-1&&input_.indexOf(")")==-1&&input_.indexOf("(")==-1&&input_.indexOf("!")==-1){
				return "";
			}
			return input_;
		}

		public int longestValueLength(/*Object*/ ArrayList<BigDecimal> arrayList){
			int longestLength0_ = 0;
			int longestLength1_ = 0;
			int currentLength0_=0;
			int currentLength1_=0;
			String[] tempStrings_={"",""};
			for(int i = arrayList.size() -1 ; i >= 0; i--){
				
				tempStrings_ = arrayList.get(i).toString().split("\\.", -1);
				currentLength0_ = tempStrings_[0].length();
				currentLength1_ = tempStrings_[1].length();

				if(currentLength0_ > longestLength0_){
					longestLength0_ = currentLength0_;
				}
				if(currentLength1_ > longestLength1_){
					longestLength1_ = currentLength1_;
				}
			}
			return longestLength0_+longestLength1_+2;
		}

		public String allToNum(String input_){
			input_ = input_+"  ";

			String simpleStrings1[] = {"first","second","third","fourth","fifth","sixth","Seventh","Eighth","nineth","tenth"};//,"eleventh","twelveth"};
			for(int i = 0; i < simpleStrings1.length; i++){
				input_ = input_.replaceAll(simpleStrings1[i],"_"+(i+1));
			}

			// String simpleStrings[][] = {{"zero","0"},{"one","1"},{"two","2"},{"three","3"},{"four","4"},{"",""}};
			String simpleStrings2[] = {"zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve"};
			for(int i = 0; i < simpleStrings2.length; i++){
				input_ = input_.replaceAll(simpleStrings2[i],i+"");
			}

			input_ = input_.replaceAll("t","");

			int firstOccurenceLocation_;
			char nextChar_ = ' ';
			while(input_.indexOf("y") != -1){
				firstOccurenceLocation_ = input_.indexOf("y");
				nextChar_ = input_.charAt(firstOccurenceLocation_+1);
				if(nextChar_ == ' '){
					nextChar_ = input_.charAt(firstOccurenceLocation_+2);
				}
				if(isCharNumber(nextChar_) || nextChar_=='.'){
					input_ = input_.replaceFirst("y","");
				}else{
					input_ = input_.replaceFirst("y","0");
				}
			}

			while(input_.indexOf("een") != -1){
				firstOccurenceLocation_ = input_.indexOf("een");
				input_ = input_.substring(0,firstOccurenceLocation_-1)+"1"+input_.substring(firstOccurenceLocation_-1,input_.length());
				input_ = input_.replaceFirst("een","");
			}

			String simpleStringsTy[][] = {{"wen","2"},{"hir","3"},{"for","4"},{"fif","5"},/**/{"hundred","00"},{"housand","000"},{"million","000000"}};
			for(int i = 0; i < simpleStringsTy.length; i++){
				input_ = input_.replaceAll(simpleStringsTy[i][0],simpleStringsTy[i][1]);//(?i)
			}

			int endStop_ = input_.length() - 2;
			if(endStop_ < 0){
				endStop_ = 0;
			}
			return input_.substring(0, endStop_);//Cleanup added extra spaces at the end
		}

		public boolean isCharNumber(char input_){
			return Character.isDigit(input_);
		}

		@Override
		public String responseTweaker(String input){
			if (language.equals(LANGUAGES.EN)){
				return input;
			}else{
				Debugger.println("Custom parameter 'FirstNumber' does not support language: " + this.language, 1);
				return input;
			}
		}

		@Override
		public String build(String input){
			//anything extracted?
			if (input.isEmpty()){
				return "";			
			//any errors?
			}else if (input.equals("<user_data_unresolved>")){
				this.buildSuccess = false;
				return ""; 		//TODO: this probably should become something like 'Interview.ERROR_USER_DATA_ACCESS' in the future;
			}else{
				//build result with entry for field "VALUE"
				JSONObject itemResultJSON = JSON.make(InterviewData.VALUE, input);
				this.buildSuccess = true;
				return itemResultJSON.toJSONString();
			}
		}		
	}

}