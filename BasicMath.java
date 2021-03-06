package net.b07z.sepia.sdk.services.uid1005;//Will need to switch to assistant user to make public to all users.

import java.util.TreeSet;

import org.json.simple.JSONObject;

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
import net.b07z.sepia.server.core.tools.Sdk;
import net.b07z.sepia.server.assist.data.Card;
import net.b07z.sepia.server.assist.data.Card.ElementType;
import net.b07z.sepia.server.core.assistant.ACTIONS;
import net.b07z.sepia.server.core.tools.JSON;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.math.*;

/**
 * BasicMath extension program
 * @author Florian Quirin (RestrauntDemo - this file's template)
 * @author 42null		  (BasicMath - this file), also, anything special about this line number? :)
 * 
 * @moreInfo: SEPIA Framework Website: https://github.com/SEPIA-Framework/
 * @moreInfo: My Personal Repository : https://github.com/42null/42null-SEPIA-Extensions/
 * @dateInstalled: 
 * @commitID: 
 * 
 * @versionName: 
 * @version #0.02.09.0
 * 
 * @TakeNote: Make shure to check your settings under #AREA and read #PEREMENT. For more info run "Whatis help"
 */

public class BasicMath implements ServiceInterface {
	
private static final String CMD_NAME = "basic_math";
private static final String versionName = "PullRequest1aVersionUncleaned";
private static final String versionNumber = "0.02.09.0";

// -------------------- #AREA FOR SETTING'S VARAIBLES --------------------
private static final boolean makePublic = false; //This controls if this extension should be avaiable for all users.
private static final boolean expermentalMode = false; //Allows you to use some expermental features not qwite implementd or that currently have bugs.
private static final boolean allowAstric = false; // #allowAstric Because "\\*" does not always work to avoid confunsion set to false. Things such as "Whatis 2*2" does work for some reason though.
private static final boolean doDisclaimers = true; //If set to false, disclaimers will not be reported.
private static final int displayMode = 1; //Will be superseeded by debugMode, currently choose from 1-3, search for #displayMode for more infomation.
private static final boolean keepSymbol = true; //If true, all displayModes will replace saying "3.14159265358979323" with "π".
private static final String[] wakeWords = {"whatis","calculate"};
// -----------------------------------------------------------------------

// ------------------------- #PEREMENT BUG AREA --------------------------
/* This area is for bugs that are outside of my control from a program 
prespective, because even just returing exactly what was given as an input
yealds a crash I am unable to fix what I cannot modify/work with. Because 
of this, please for your own conveance try to avoid triggureing these 
bugs. They will not always trigger when you use them so it is just best to
prevent their avability. In most cases just putting 0+ before your program 
will fix the issue. I tried having it automaticaly put 0+ automaticaly 
but the crash happens before I can access the users input meaning nothing 
can be done.

1.) "Zero"
2.) Without 0+, n1/n1
3.) /0, this cannot be fixed, I believe somehting is processed before I have access to it.
4.) n1"teen" (ex thirteen), even after extensive testing. Please use "threeteen", "thir teen", "3teen", or "13".
5.) Negitive pi, (0-pi) still works though. Something avout "negpi" or negitive pi thows off the proccesser and I have done everything in my power of fix it.
6.) ni^0 does not work, you need 0+ first for some reason.

Please contact me if you know what is crashing SEPIA before I can view the string in these cases. I am shure this is not an intended feature of whater pre-proccesing SEPIA does.

Other features that have not been included (ether I have been unable to find the source or have spent way too many hours that I will never get back)
- n1 to the n2th power dosen't but n1 to the power of n2 works
- n1 ^ 0 does not work because of proccessing with BigDouble, you should know this always returns 1 though.
- When writing combinations of large numbers (ty,hundred,thousand,etc) if they contain smaller demonations (eg three thousand one), this program will incorectly identify them. This is why displayMode will show you what was realy caulacted. Just type 3001

*/
// -----------------------------------------------------------------------



	@Override
	public TreeSet<String> getSampleSentences(String lang) {
		TreeSet<String> samples = new TreeSet<>();
		//GERMAN
		// if (lang.equals(Language.DE.toValue())){
		// //OTHER
		// }else{
			samples.add("Whatis help");
			samples.add("Whatis version");
			samples.add("Whatis neg 5.43331 times (nineteen / forty seven point nine 7) plus (0 - pi) to the power of neg four?");
		// }
		return samples;
	}
	
	@Override
	public ServiceAnswers getAnswersPool(String language) {
		ServiceAnswers answerPool = new ServiceAnswers(language);
		
		//Build English answers
		if (language.equals(LANGUAGES.EN)){
			if(displayMode == 1){ // Return 
				answerPool.addAnswer(successAnswer, 0, "Ok, <1>");
			}else if(displayMode == 2){
				answerPool.addAnswer(successAnswer, 0, "Ok, <1>");
			}else if(displayMode == 3){
					answerPool.addAnswer(successAnswer, 0, "Ok, <1>");
			}else{
				answerPool.addAnswer(successAnswer, 0, "You did not select a valid number for your displayMode. <1>");
			}
			answerPool
								
				.addAnswer(okAnswer, 0, "Sorry there has been an error @okAnswer")
				.addAnswer(askFirstNumber, 0, "Sorry, it appears that you did not enter any operators, could you please add some?")
						// Answer.Character.neutral, 2, 5))
				.addAnswer(new Answer(Language.from(language), askFirstNumber, "Sorry I did not understand your first number,"
								+ "Could you tell me once more please?", 
				Answer.Character.neutral, 2, 5))
						//example of how to use the 'complete' answer object
				;
			return answerPool;
		
		//Other languages are not used in this project yet
		}else{
			return null;
		}
	}
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
					+ "(|caulacate)\\b.* (+|-|/|^)"
				+ ")\\b.*", EN);*/

		String tempString = "";
		for(int i = 0; i < wakeWords.length; i++){
			tempString += wakeWords[i]+"|";
		}
		tempString = tempString.substring(0,tempString.length()-1);
		info.setCustomTriggerRegX("\\b("+tempString+")\\b",EN);

		info.setCustomTriggerRegXscoreBoost(10);		//boost service a bit to increase priority over similar ones
		
		//Parameters:
		//Required parameters will be asked automatically by SEPIA using the defined question.
		
		Parameter p1 = new Parameter(new GetFirstNumber())
				.setRequired(true)
				.setQuestion(askFirstNumber);
		
		info.addParameter(p1);//.addParameter(p2).addParameter(p3);
		
		//Answers (these are the default answers, you can trigger a custom answer at any point in the module 
		info.addSuccessAnswer(successAnswer)
			.addFailAnswer(failAnswer)
			.addOkayAnswer(okAnswer);
		
		//Add answer parameters that are used to replace <1>, <2>, ... in your answers.
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
		
		
		//Set answer parameters as defined in getInfo():
		api.resultInfoPut("firstNumber", firstNumber);
		
		if((firstNumber).contains("--{:Help Page:}--")){
			api.addAction(ACTIONS.BUTTON_IN_APP_BROWSER);
			api.putActionInfo("url", "https://github.com/42null/42null-SEPIA-Extensions");
			api.putActionInfo("title", "Source code");
			
			Card card = new Card(Card.TYPE_SINGLE);
			JSONObject linkCard = card.addElement(
					ElementType.link, 
					JSON.make("title", "BasicMath Documentation" + ":", "desc", "By 42null"),
					null, null, "", 
					"https://github.com/42null/42null-SEPIA-Extensions/blob/master/README.md", 
					"https://sepia-framework.github.io/img/icon.png", 
					null, null
			);
			JSON.put(linkCard, "imageBackground", "#000");	//more options like CSS background
			api.addCard(card.getJSON());
		}else if((firstNumber).contains("getting version info...")){
			// api.addAction(ACTIONS.BUTTON_IN_APP_BROWSER);
			// api.putActionInfo("url", "https://github.com/42null/42null-SEPIA-Extensions");
			// api.putActionInfo("title", "Homepage");
			
			Card card = new Card(Card.TYPE_SINGLE);
			JSONObject linkCard = card.addElement(
					ElementType.link, 
					JSON.make("title", "BasicMath Version Information" + ":", "desc", "Version Name: <u>"+versionName+"</u><br> VersionID: <u>"+versionNumber+"</u><br> Github CommitID: <u>Unavaible</u>"),
					null, null, "", 
					"https://github.com/42null/42null-SEPIA-Extensions/blob/master/README.md", 
					"https://sepia-framework.github.io/img/icon.png", 
					null, null
			);
			JSON.put(linkCard, "imageBackground", "#000"/*999*/);	//more options like CSS background
			api.addCard(card.getJSON());
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
		char dontUseMeChar = 'j';//TODO: Add a detection to check if this character is used, is this realy nessery though?
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
			ArrayList<Integer> collectedItemArrayMeta_ = new ArrayList<Integer>();
			ArrayList<BigDecimal> collectedNumberArray_ = new ArrayList<BigDecimal>();

			// if(true){return input;}
			// input = input.replaceAll("zero","0");
			input = input.replaceAll(" ","");
			String extracted = removeUnwanted(/*"0plus"+*/input+" ");// ' ' required to add ending seperation
			// if(true){return extracted;}
			// #SpecialPages;
			if(extracted.equals("")){
				return "removeUnwanted(input+' ') returned an empty string";
			}else if(extracted.equals("--help")){
				pageToReturn = 1;
				return returnHelp();
			}else if(input.contains("test")){
				return "This is BasicMath running, I was able to detect your usage of 'test'.";
			}else if(input.contains("version")){
				return "getting version info...";
			}else if(extracted.contains("π")){//"--pi")){
				disclaimerPi = true;
			}else if(extracted.contains("!")){
				disclaimerFactorial = true;
			}

			if(extracted.equals("(π) ")){// Special Case
				return "π would be 3.14159265358979323, for more digits see https://www.piday.org/million/.";
			}
			// if(true){return ">"+extracted+"<";}

			if(extracted.indexOf("+")==-1&&extracted.indexOf("-")==-1&&extracted.indexOf("^")==-1&&extracted.indexOf("✕")==-1&&extracted.indexOf("÷")==-1&&extracted.indexOf("(")==-1&&extracted.indexOf(")")==-1&&extracted.indexOf("(")==-1&&extracted.indexOf(/*"\\b(!)\\b"*/"f")==-1){
				if(allowAstric){
					return "Error: No operators detected";
				}else{
					return "Error: No operators detected. If you used an astric please note that allowAstric is currently set to false.";	
				}
			}
			
			while(extracted.charAt(0) == ' ' && !(extracted.equals(""))){
				extracted = extracted.substring(1,extracted.length()-1);
			}

			extracted = extracted.replaceAll(dontUseMeStr,"");

			//English
			if (this.language.equals(LANGUAGES.EN)){
				// debugStr = extracted;
				char currentChar_;
				String currentItem_ = "";
				int currentItemMeta_;
				boolean canAdd = false;
				BigDecimal intermideate_;
				String recreated_;
				// Boolean whileRan_ = false;
				extracted = extracted.replaceAll("zero","0");  //Last ditch resort
				extracted = extracted.replaceAll("−π","(0-π)");//Last ditch resort
				extracted = "("+extracted+")"+dontUseMeStr;
				extracted = extracted.replaceAll(" ","");
// ItemArayMeta: 0 = unknown/error, 1 = number, 2 = operator, 2 = wrapper ( '(' or ')' )
				boolean notANumber = true;

				for(int i = 0; i < extracted.length()-1; i++){
					currentChar_ = extracted.charAt(i);

					if(isCharNumber(currentChar_) || currentChar_ == '.' || ((currentChar_ == '−') && (extracted.charAt(i+1) != 'π'))){
						notANumber = false;

						if(currentChar_ == '−'){
							currentChar_ = '-';
						}

						currentItem_ += currentChar_;

						if(!(isCharNumber(extracted.charAt(i+1)) || (extracted.charAt(i+1)) == '.')){
							intermideate_ = new BigDecimal(currentItem_+"");
							numberArray.add(intermideate_);
							itemArrayMeta.add(1); //Number
							currentItem_ = "";
						}

// operatorArray: 0 = unknown/error, + = 1, - = 2, ✕ = 3, ÷ = 4
					}else if(currentChar_ == '+'){
						itemArrayMeta.add(2); //Operator +
					}else if(currentChar_ == '-'){// && extracted.charAt(i) != '−'){
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
					}else if(("j"+currentChar_).equals("j!") || currentChar_ == 'f'){
						if(expermentalMode){
							itemArrayMeta.add(9); //Operator ! (factorial)
						}
					}else if(currentChar_ == 'π'){	//Sepcial number case (pi)
						notANumber = false;
						itemArrayMeta.add(1); //Number
						numberArray.add(new BigDecimal("3.14159265358979323"));//846
						currentItem_ = "";
					// }else if(currentChar_ == '−' && (extracted.charAt(i+1) == 'π')){	//Sepcial number case (neg pi)
					// 	notANumber = false;
					// 	itemArrayMeta.add(1); //Number
					// 	numberArray.add(new BigDecimal("3.14159265358979323"));//846
					// 	// extracted = extracted.substring(0,i-1)+extracted.substring(i+1,extracted.length());
					// 	extracted = extracted.replaceAll("π","");
					// 	currentItem_ = "";
					// 	// return extracted;
					}else if(currentChar_ == '_' && expermentalMode){ //Special case - out of order powers
						notANumber = false;
						// 2_4^
						// 2^4
						itemArrayMeta.add(1); //Number
						numberArray.add(new BigDecimal(Double.parseDouble(currentItem_.substring(0,currentItem_.length()-2))+""));
						extracted = extracted.replaceFirst("^","");
						extracted = extracted.replaceFirst("_","^");
						// extracted = extracted.substring(0,i)+"^"+extracted.charAt(i+1)+extracted.substring(0,i);
						currentItem_ = "";
						// return extracted;
					}else{
						notANumber = false;
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
					
					// itemArrayMeta.add(1,1);
					// numberArray.add(1,new BigDecimal("0"));
					// itemArrayMeta.add(2,2);
					// numberArray.add(2,null);

					if(displayMode == 2){//For Fancy
						addInMultiplication();
						collectedItemArrayMeta_ = new ArrayList<Integer>(itemArrayMeta);
						collectedNumberArray_ = new ArrayList<BigDecimal>(numberArray);
						recreated_ = recreateInput(collectedItemArrayMeta_,collectedNumberArray_);
						itemArrayMeta = new ArrayList<Integer>(collectedItemArrayMeta_);
						numberArray = new ArrayList<BigDecimal>(collectedNumberArray_);
					}else{
						// removeSingularSections();
						addInMultiplication();
						collectedItemArrayMeta_ = new ArrayList<Integer>(itemArrayMeta);
						collectedNumberArray_ = new ArrayList<BigDecimal>(numberArray);
						recreated_ = recreateInput(collectedItemArrayMeta_,collectedNumberArray_);
					}

					// for(int i = 0; i < itemArrayMeta.size();i++){
					// 	// if(numberArray.get(i).compareTo(new BigDecimal(3.14159265358979323))==0){
					// 	if((numberArray.get(i)+"").equals(""+(new BigDecimal(3.14159265358979323)))){
					// 		// itemArrayMeta.add(i+1,8);
					// 		// itemArrayMeta.add(i,7);
					// 		// numberArray.add(i+1,null);
					// 		// numberArray.add(i,null);
					// 	}
					// }

					// removeSingularSections();
					addInMultiplication();
					orderOfOperations();
					itemArrayMeta.add(0,7);
					itemArrayMeta.add(8);
					numberArray.add(0,null);
					numberArray.add(null);
					//FOR -DEBUG
					str7 += extracted+"";
					str7 += collectedItemArrayMeta_;
					//END -DEBUG
					while(itemArrayMeta.contains(2)||itemArrayMeta.contains(3)||itemArrayMeta.contains(4)||itemArrayMeta.contains(5)||itemArrayMeta.contains(6)||itemArrayMeta.contains(9)){
						caulactedNumber = splitAndConquer();
						orderOfOperations();//Is this nessery?
					}
					

	// Include?
				// String returnThis_ = "";
				// if(false){ returnThis_ += "["+extracted+"] ";}
				// if(false){  returnThis_ += "itemArrayMeta: ["+itemArrayMeta+"] ";}
				// if(false){ returnThis_ += "numberArray: ["+numberArray+"] ";}
				// if(false){  returnThis_ += "operatorArray: ["+operatorArray+"] ";}
				// if(false){  returnThis_ += "debugDouble: ["+debugDouble+"] ";}
				// if(false){ returnThis_ += "debugStr: ["+debugStr+"] ";}

	// Remove .0 artificat from caulactedNumber: Cannot get index of "."
				String caulactedNumberStr = caulactedNumber+"";
				// if((caulactedNumber.doubleValue() % 1) == 0 && !(caulactedNumberStr.contains("E+")) && caulactedNumberStr.contains("\\.")){
				// 	caulactedNumberStr = caulactedNumberStr.substring(0,caulactedNumberStr.indexOf("\\."));
				// }
// Start Disclaimers
				if(doDisclaimers){
					if(disclaimerPi){
						caulactedNumberStr +=", for π the approximation of '3.14159265358979312' was used.";
					}
					if(disclaimerFactorial){
						caulactedNumberStr += " Factorial (!) was used, please note that due to the structure of this program your answer may not be acurate.";
					}
				}
// End Disclaimers
// Start Cleanup recreated_ with pi
				if(keepSymbol){//Convert 3.14159265358979323 to pi
					recreated_ = recreated_.replaceAll("3.14159265358979323","π");
				}
				// recreated_ = recreated_.replaceAll("\\(π)","π");//This does not work because of the usage of ( and )
// End Cleanup recreated_ with pi

				if(debugMode){
					// return debugDouble+""+" debugStr =_"+debugStr+/*" operatorArray: "+operatorArray+" itemArrayMeta: "+itemArrayMeta+*/"_ numberArray: "+numberArray+/*" extracted: _"+extracted+*/"_ str7:"+str7;
					String returnThis = caulactedNumber+"_";
					returnThis += "numberArray: "+numberArray+"_";
					returnThis += "itemArrayMeta: "+itemArrayMeta+"_";
					returnThis += "str7: "+str7+"_";
					// returnThis += "numberPlace: "+numberPlace+"_";
					// returnThis += "extracted: "+extracted+"_";
					return returnThis;
	// #displayMode
				}else if(displayMode == 1){ // Return 
					return recreated_ +" would be "+caulactedNumberStr+""; // Return answer with extracted (also changes return statement)
				}else if(displayMode == 2){ // Return 
					return recreated_ +" would be "+caulactedNumberStr+""; // Return answer with extracted (also changes return statement) and enables fancy
				}else if(displayMode == 3){ // Just return the answer (and disclaimers assumeing there are any)
					return caulactedNumberStr+"";
				}else{
					return "Error at #displayMode, you need to set a valid number";
				}
	//Other languages
			}else{
				Debugger.println("Custom parameter 'firstNumber' does not support language: " + this.language, 1);
				return "<Sorry but this does not support your language at this time.>";
			}
		}
		
		public String recreateInput(ArrayList<Integer> itemArrayMeta_, ArrayList<BigDecimal> numberArray_){
			String returnThis_ = "";
			int currentOperator_ = 10;
			char addThisArray_[] = {' ',' ','+','-','✕','÷','^','(',')','!'};

			for(int i= 0; i < itemArrayMeta_.size(); i++){
				currentOperator_ = itemArrayMeta_.get(i);
				if(currentOperator_ == 1){
					returnThis_+=numberArray_.get(i)+"";}
				else if(currentOperator_ <= 9){
					returnThis_+=addThisArray_[currentOperator_];
				}
				// else if(currentOperator_ == 2){returnThis_+="+";}
				// else if(currentOperator_ == 3){returnThis_+="-";}
				// else if(currentOperator_ == 4){returnThis_+="✕";}
				// else if(currentOperator_ == 5){returnThis_+="÷";}
				// else if(currentOperator_ == 6){returnThis_+="^";}
				// else if(currentOperator_ == 7){returnThis_+="(";}
				// else if(currentOperator_ == 8){returnThis_+=")";}
				// else if(currentOperator_ == 9){returnThis_+="f";}//!
				else{returnThis_ = " There has been an error at recreateInput";} // ' ' is for automatic removal
			}
			return returnThis_.substring(1,returnThis_.length()-1);
		}


		public void addInMultiplication(){
			for(int i = 1; i < itemArrayMeta.size()-1; i++){
				if((itemArrayMeta.get(i)==8 && itemArrayMeta.get(i+1)==1) || (itemArrayMeta.get(i)==1 && itemArrayMeta.get(i+1)==7)){
					itemArrayMeta.add(i+1, 4); //✕
					numberArray.add(i+1,null);
					i++;
				}
			}
		}

		// 1 6 1 
		// 2 n 3 

		// public void fixOutOfPlacePowers(){
		// 	for(int i = 1; i < itemArrayMeta.size()-1; i++){
		// 		if(itemArrayMeta.get(i)==1 && itemArrayMeta.get(i+1)==1 && itemArrayMeta.get(i+2)==6){
		// 			itemArrayMeta.remove(i+2);
		// 			itemArrayMeta.add(i+1, 6); // ^
		// 			numberArray.remove(i+2);
		// 			numberArray.add(i+1,null);
		// 			i = 1;//TODO: Can I make this more efficent?
		// 		}
		// 	}
		// }

		// public void removeSingularSections(){
		// 	if(true){return;}
		// 	for(int i = 1; i < itemArrayMeta.size()-1; i++){//Put in if i first?
		// 		// if(numberArray.size() == itemArrayMeta.size()){str7 = "yikes";}
		// 		if(itemArrayMeta.get(i)==1 && itemArrayMeta.get(i-1)==7 && itemArrayMeta.get(i+1)==8){
		// 			itemArrayMeta.remove(i+2);
		// 			itemArrayMeta.remove(i-1);
		// 			numberArray.remove(i+2);
		// 			numberArray.remove(i-1);
		// 			i = 1;//TODO: Can I make this more efficent?
		// 		}
		// 	}
		// }
		
		// public void removeSingularSections(ArrayList<Integer> itemArrayMeta_, ArrayList<BigDecimal> numberArray_){
			// for(int i = 1; i < itemArrayMeta.size()-1; i++){//Put in if "i" first?
			// 	if(itemArrayMeta_.get(i)==1 && itemArrayMeta.get(i-1)==7 && itemArrayMeta.get(i+1)==8){
			// 		itemArrayMeta_.remove(i+2);
			// 		itemArrayMeta_.remove(i-1);
			// 		numberArray_.remove(i+2);
			// 		numberArray_.remove(i-1);
			// 		i = 1;//TODO: Can I make this more efficent?
			// 	}
			// }
			/*
			int timesOccured = 0;
			int firstOccurenceLocation = -1;

			// for(int i = 1; i < itemArrayMeta.size()-1; i++){//Put in if "i" first?
			// 	if(itemArrayMeta_.get(i)==7 && itemArrayMeta_.get(i+1)==7){//Make more efficent - indexOf()?
			// 		firstOccurenceLocation = i;
			// 		i = 1;//TODO: Can I make this more efficent?
			// 	}else if(itemArrayMeta_.get(i)==8 && itemArrayMeta_.get(i+1)==8){
			// 		if(i != -1){

			// 		}
			// 	}else if(itemArrayMeta_.get(i)==8 && itemArrayMeta_.get(i+1)!=8){
			// 		firstOccurenceLocation = -1;
			// 	}
			// }
//FUTURE: Note, if the number array grows and numbers above 9 are used, this code will need to be updated
			String stringyItems_ = (itemArrayMeta_+"").replaceAll(",","");
			stringyItems_ = stringyItems_.substring(1,stringyItems_.length()-1);
			int openPartPlace_ = stringyItems_.indexOf("77");
			while(stringyItems_.charAt(openPartPlace_+1)==7){
				openPartPlace_++;
			}
			int shutPartPlace_ = stringyItems_.indexOf("88");
			String subString_;
			while(openPartPlace_!=-1 && shutPartPlace_!=1){
				subString_ = stringyItems_.substring(openPartPlace_+1,shutPartPlace_);
				if(hasOf(subString_,"7") == hasOf(subString_,"8")){
					stringyItems_ = subString_;
					numberArray_ = new ArrayList<BigDecimal>(numberArray_.subList(openPartPlace_,shutPartPlace_+1));
				}
			}
*/
		// }

		// Is not used but it is nice to have
		public int hasOf(String main_,String minor_){
			return main_.length()-main_.replaceAll(minor_, "").length();
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
			// 7, 7, 1, 2, 7, 1, 9, 8, 8
			int operators_[] = {6,6,5,4};//,3,2};
			int currentOperatorNum_;
			if(expermentalMode){
				currentOperatorNum_ = 9;
				// SPECIAL CASE: factorial //TODO: merge into one again?
				for(int i = 0; i < itemArrayMeta.size(); i++){
					currentMeta_ = itemArrayMeta.get(i);
					innerGo_ = true;
					if(currentMeta_ == currentOperatorNum_){
						j = i;
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
			// str7 += "*"+itemArrayMeta_+""+numberArray_+"*";
			BigDecimal presentNumber_ = numberArray_.get(0);
			BigDecimal currentNum_ = new BigDecimal("0");
			// BigDecimal lastNum_;
			int currentOperator_ = -1;
			int currentMeta_ =-1;
			int lastMeta_ = -1;
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
					if(currentNum_ == null){
						// return "there was a problem";
					}
					// 2+2f+0
					// 121921
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
						// if(currentNum_.intValue() == 0){//Not working for ^ 0
						// 	presentNumber_ = new BigDecimal("1");
						// }else{
							presentNumber_ = presentNumber_.pow(currentNum_.intValue(),mc_);
						// }
					}
				/*}else{*/ //If an operator
				}else if(currentMeta_ == 9){ //factorial
					for(int j = currentNum_.intValue()-1; j > 1; j--){
						presentNumber_ = presentNumber_.multiply(new BigDecimal(j),mc_);
						// str7 += "*"+presentNumber_+"*";
						// str7 += ":"+(presentNumber_.multiply(new BigDecimal(j),mc_)+":");
					}
				}
				lastMeta_ = currentMeta_;
			}

			numberArray.remove(locationTakenFrom_-1);//Removes "("
			itemArrayMeta.add(locationTakenFrom_,1);
			numberArray.add(locationTakenFrom_,presentNumber_);

			return presentNumber_;
		}

		public static String returnHelp(){
			String returnThis_;
			returnThis_ =  "--{:Help Page:}-- ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ";// \n
			returnThis_ += "BasicMath is a SDK extenson that uses the 'Whatis', 'Caulacate', or other custom commands to solve and return answers to simple math problems. An extensive documentation at https://github.com/42null/42null-SEPIA-Extensions has been created. Make shure you are viewing the correct version. To see which version you are on run 'Whatis version'. If you are having problems with BasicMath crashing, check line #71";
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
			// if(true){return input_;}
			input_ = input_.toLowerCase();

			if(input_.contains("help")){
				return "--help";
			}else if(input_.contains("debug")){
				debugMode = true;
				input_ = input_.replaceAll("debug","");
			}

// "Wake" words
			String tempString_="";
			for(int i = wakeWords.length-1; i >= 0; i--){//Keep with '>='? (trying to keep it as lightweight on processing as possable)
				// tempString_ += wakeWords[i]+"|";
				input_ = input_.replaceAll(wakeWords[i], "");
			}
			// tempString_ = tempString_.substring(0,tempString_.length()-2);//Not useing removeLast or removeSpace to save on processing resources
			// input_ = input_.replaceAll("\\b("+tempString_+")\\b", "");

			input_ = input_.replaceAll("eighty","80"); //For problems with eighty //TOD: Find a better solution to this problem

// String to num converter
			input_ = allToNum(input_);
			// if(true){return input_;}

// OPERATORS
			input_ = input_.replaceAll("multiplied", "✕");
			input_ = input_.replaceAll("times", "✕");
			input_ = input_.replaceAll("divided", "÷");
			input_ = input_.replaceAll("/", "÷");
			// input_ = input_.replaceAll("\\b(plus|added)\\b", "+");
			input_ = input_.replaceAll("plus", "+");
			input_ = input_.replaceAll("added", "+");
			
			// input_ = input_.replaceAll("\\b(minus|subtracted|subtacts)\\b", "-");
			input_ = input_.replaceAll("minus", "-");
			input_ = input_.replaceAll("subtracted", "-");
			input_ = input_.replaceAll("subtacts", "-");

			input_ = input_.replaceAll("pow", "^");
			if(allowAstric){input_ = input_.replaceAll("\\*", "✕");// #allowAstric
			for(int i = 0; i < input_.length(); i++){
				if((input_.charAt(i) == 'x') && (input_.charAt(i-1) != 'i')){//To avoid six
					input_ = input_.substring(0, i)+"✕"+input_.substring(i+1);}
			}}
			input_ = input_.replaceAll("\\!", "f");
			// input_ = input_.replaceAll("\\b(!)\\b", "f");
			// input_ = input_.replaceAll("\\b(*)\\b", "✕");

// SPECIAL CASES
			input_ = input_.replaceAll("neg","−");//−");
			input_ = input_.replaceAll("pi","(π)");//3.14159265358979323846
			input_ = input_.replaceAll("squared","^2");
			input_ = input_.replaceAll("cubed","^3");
			// input_ = input_.replaceAll("\\b(point|dot)\\b", "ź");// "∙"
			input_ = input_.replaceAll("point","\\.");
			input_ = input_.replaceAll("dot","\\.");

// FILLER/SPACER WORDS
			input_ = input_.replaceAll(",", "");
			input_ = input_.replaceAll("by", "");
			input_ = input_.replaceAll("to", "");
			input_ = input_.replaceAll("of", "");
			input_ = input_.replaceAll("by", "");
			input_ = input_.replaceAll("the", "");
			input_ = input_.replaceAll("and", "");
			input_ = input_.replaceAll("itive", ""); //(For negitive)
			input_ = input_.replaceAll("er", ""); //(For power)
			

			// while(input_.contains("ź")){
			// 	String tempInputA_;
			// 	String tempInputB_;	
			// 	for(int i = 0; i < input_.length(); i++){
			// 		if(input_.charAt(i) == 'ź'){
			// 			// if(input_.charAt(i-1) == ' '){
			// 			// 	tempInputA_ = (input_.substring(0,i-1))+".";
			// 			// }else{
			// 			// 	tempInputA_ = (input_.substring(0,i))+".";
			// 			// }
			// 			// if(input_.charAt(i+1) == ' '){
			// 			// 	tempInputB_ = input_.substring(i+2,input_.length()-1);
			// 			// }else{
			// 			// 	tempInputB_ = input_.substring(i+1,input_.length()-1);
			// 			// }
			// 			input_ = (input_.substring(0,i))+"."+input_.substring(i+1,input_.length());
			// 			// tempInputB_ = input_.substring(i+1,input_.length()-1);
			// 			// input_ = tempInputA_ + tempInputB_;
			// }	}	}
			// input_+="ttt";
			// input_ = input_.replaceAll(" ","");
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

			String simpleStrings1[] = {"first","second","third","fourth","fifth","sixth","seventh","eighth","nineth","tenth"};//,"eleventh","twelveth"};
			for(int i = 0; i < simpleStrings1.length; i++){
				input_ = input_.replaceAll(simpleStrings1[i],("_"+(i+1)));
			}

			// input_ = input_.replaceAll(simpleStrings2[i],i+"");
			// String simpleStrings[][] = {{"zero","0"},{"one","1("},{"two","2"},{"three","3"},{"four","4"},{"",""}};
			// input_ = input_.replaceAll("zero","0");
			// input_ = "zero+fivetwo";
			// if(true){return input_;}
			String simpleStrings2[] = {"zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve"};
			for(int i = 0; i < simpleStrings2.length; i++){
				// if(i==0){input_ = input_.replaceAll("zero","0");}else{
				input_ = input_.replaceAll(simpleStrings2[i],(""+i+""));//}
			}

			int firstOccurenceLocation_;
			char nextChar_ = ' ';
			while(input_.indexOf("ty") != -1){
				firstOccurenceLocation_ = input_.indexOf("ty");
				nextChar_ = input_.charAt(firstOccurenceLocation_+1);
				if(isCharNumber(nextChar_) || nextChar_=='.'){
					input_ = input_.replaceFirst("ty","");
				}else{
					input_ = input_.replaceFirst("ty","0");
				}
			}

			// input_ = input_.replaceAll("zero","0");
			char nextPlace_;// = ' ';
			String simpleStringsTy[][] = {{"twen","2"},{"thir","3"},{"for","4"},{"fif","5"},/**/{"hundred","00"},{"thousand","000"},{"million","000000"},{"billion","000000000"},{"trillion","000000000000"}};
			for(int i = 0; i < simpleStringsTy.length; i++){
				input_ = input_.replaceAll(simpleStringsTy[i][0],simpleStringsTy[i][1]);//(?i)
				// while(input_.contains("j")){
				// 	// nextPlace_ = );
				// 	if("1234567890".contains(input_.charAt(input_.indexOf("j")+2)+"")){

				// 	}
				// 	// 300j0j0five
				// }
			}

			while(input_.contains("teen")){
				firstOccurenceLocation_ = input_.indexOf("teen");
				input_ = input_.substring(0,firstOccurenceLocation_-1)+"1"+input_.substring(firstOccurenceLocation_-1,input_.length());
				input_ = input_.replaceFirst("teen","");
			}

			// int endStop_ = input_.length() - 2;
			// if(endStop_ < 0){
			// 	endStop_ = 0;
			// }
			return input_;//.substring(0, endStop_);//Cleanup added extra spaces at the end
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
				// return "";
				return "Error <user_data_unresolved>"; 		//TODO: this probably should become something like 'Interview.ERROR_USER_DATA_ACCESS' in the future;
			}else{
				//build result with entry for field "VALUE"
				JSONObject itemResultJSON = JSON.make(InterviewData.VALUE, input);
				this.buildSuccess = true;
				return itemResultJSON.toJSONString();
			}
		}		
	}

}
