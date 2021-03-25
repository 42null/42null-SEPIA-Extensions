// TODO: If search is black, allow bringing to search page or ask more questions


package net.b07z.sepia.sdk.services.uid1003;//Will need to switch to assistant user to make public to all users.

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

/**
 * BasicMath extension program
 * @author Florian Quirin (RestrauntDemo - this file's template)
 * @author 42null		  (CustomWebSearch - this file), also, anything special about this line number? :)
 * 
 * @moreInfo: SEPIA Framework Website: https://github.com/SEPIA-Framework/
 * @moreInfo: My Personal Repository : https://github.com/42null/42null-SEPIA-Extensions/
 * @dateInstalled: 
 * @commitID: 
 * 
 * @versionName: 
 * @version #0.00.01.0 //First '1' is for the version submitted for a pull request onto the official SEPIA. Future versions will go up in order but if not submited will contain a '0' instead.
 * 
 * Testing here...
 * 
 * @TakeNote: Make shure to check your settings under #AREA and read #PEREMENT. For more info run "CustomWebSearch help"
 */

public class CustomWebSearch implements ServiceInterface {
	
	private static final String CMD_NAME = "custom_web_search";
	private static final String versionName = "PullRequest1aCleaned";
	private static final String versionNumber = "1.02.09.1";
	
	// -------------------- #AREA FOR SETTING'S VARAIBLES --------------------
	private static final boolean makePublic = true; //This controls if this extension should be avaiable for all users.
	private static final boolean expermentalMode = false; //Allows you to use some expermental features not qwite implementd or that currently have bugs.
	private static final boolean doDisclaimers = true; //If set to false, disclaimers will not be reported.
	private static final boolean debugMode = false; //Enables extra outputs
	//<website operator>, <starturl>, <spaces>
	private static final String[][] websiteList = {//To work with '?' marks you will need to replace them wilth a #@, if you would like to change this, search and replace this string with something else
	// Default working examples:
	// {"!!!","negitive array posistion found","!"},

	//{ <Trigger word > , <url format before ending> , <space replacement operator>, <url ending format>}
	{"youtube,","https://www.youtube.com/results#@search_query=","+", ""},//Defaut to search when not found
	{"wikihow","https://www.wikihow.com/wikiHowTo#@search=","+",""},
	{"wikipedia","https://www.en.wikipedia.org/wiki/","_",""},
	{"duck","https://www.duckduckgo.com/#@q=","+","&ia=web"},//Need to do search for "?"
	{"_","_","_","_"}
	};
	// -----------------------------------------------------------------------

/* Currently (and maby never will) does not support
	- Recursive search operators; searching for the space replacements name as part of the paramater, no real reason to do this in the first place though.
	-
*/


	//Define some sentences for testing:
	@Override
	public TreeSet<String> getSampleSentences(String lang) {
		TreeSet<String> samples = new TreeSet<>();
			samples.add("Search Wikihow for pineapple");
		return samples;
	}
	
	@Override
	public ServiceAnswers getAnswersPool(String language) {
		ServiceAnswers answerPool = new ServiceAnswers(language);
		
		//Build English answers
		// if (language.equals(LANGUAGES.EN)){
		if(debugMode){
			answerPool.addAnswer(successAnswer, 0, "CustomWebSearch Returning: '<1>'");
		}else{
			// answerPool.addAnswer(successAnswer, 0, "<1>");//TODO: Change to allow .substring(), want to be able to just state a link
			String possableStatements[] = {
			"Here you go!",
			"Try clicking this! :D",
			"I believe this is what you are looking for...",
			"v V v V v V  (See Below)  V v V v V v",
			"I don't have the answer, but this should help! :)"
			};
			String finishStatement_ = possableStatements[(int)(Math.random()*((possableStatements.length)))];
			answerPool.addAnswer(successAnswer, 0, finishStatement_);//TODO: Change to allow .substring(), want to be able to just state a link
		}
			// answerPool.addAnswer(successAnswer, 0, "CustomWebSearch responds with...");
			// answerPool.addAnswer(successAnswer, 0, "CustomWebSearch says...");
			answerPool
								
				.addAnswer(okAnswer, 0, "Sorry there has been an error @okAnswer")
				// .addAnswer(askwebsiteToSeachThrough, 0, "Sorry, it appears that you did not enter any operators, could you please add some?")
						// Answer.Character.neutral, 2, 5))
				.addAnswer(new Answer(Language.from(language), askwebsiteToSeachThrough, "Sorry I did not understand your first number,"
								+ "Could you tell me once more please?", 
				Answer.Character.neutral, 2, 5))
						//example of how to use the 'complete' answer object
				;
			return answerPool;
		
	}
	private static final String failAnswer = "error_0a";
	private static final String successAnswer = ServiceAnswers.ANS_PREFIX + "restaurant_success_0a";
	private static final String okAnswer = ServiceAnswers.ANS_PREFIX + "restaurant_still_ok_0a";

	private static final String askwebsiteToSeachThrough = ServiceAnswers.ANS_PREFIX + "simpleMath_ask_first_Number_1a";

// Globalish varaibles;

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
		info.setCustomTriggerRegX("search",EN);
		info.setCustomTriggerRegX("\\b("+"search"+")\\b",EN);



		info.setCustomTriggerRegXscoreBoost(5);		//boost service a bit to increase priority over similar ones
		
		//Parameters:
		//Required parameters will be asked automatically by SEPIA using the defined question.
		
		Parameter p1 = new Parameter(new GetWebsiteParameter())
				.setRequired(true)
				.setQuestion(askwebsiteToSeachThrough);
		
		info.addParameter(p1);//.addParameter(p2).addParameter(p3);
		
		//Answers (these are the default answers, you can trigger a custom answer at any point in the module 
		//with serviceBuilder.setCustomAnswer(..)):
		info.addSuccessAnswer(successAnswer)
			.addFailAnswer(failAnswer)
			.addOkayAnswer(okAnswer);
			//.addCustomAnswer("askTimeAndDate", askTimeAndDate); 	//optional, just for info
		info.addAnswerParameters("websiteToSeachThrough");//, "number", "name"); 	//<1>=time, <2>=number, ...
		
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
		Parameter nameParameter = nluResult.getRequiredParameter(GetWebsiteParameter.class.getName());
		String websiteToSeachThrough = nameParameter.getValueAsString();
		
		
		//Set answer parameters as defined in getInfo():
		api.resultInfoPut("websiteToSeachThrough", websiteToSeachThrough);
		
		// ... here you would call your reservation method/API/program ...

		// /*boolean wasSent =*/ service.sendFollowUpMessage(nluResult.input, service.buildResult());
		//Just for demo purposes we add a button-action with a link to the SDK



		// if(websiteToSeachThrough){}
		int websiteNum_ = Integer.parseInt(websiteToSeachThrough.substring(0,3));
		/*Change to www.____.com*/String searchingOn_ = websiteList[websiteNum_][1];
		// searchingOn_ = searchingOn_.replaceFirst("https://+","").replaceFirst("http://+","");
		// searchingOn_ = searchingOn_.substring(0, searchingOn_.indexOf("/+"));
		// searchingOn_ = searchingOn_.substring(0,1).toUpperCase()+websiteList[websiteNum_][0].substring(1);
		String searchingFor_ = websiteToSeachThrough.substring(3).replaceAll("/+","");
		/*Change to www.____.com*/ searchingOn_ = websiteList[websiteNum_][0].substring(0,1).toUpperCase()+websiteList[websiteNum_][0].substring(1);


		
		// searchingFor_ = searchingFor_.replaceAll((websiteList[websiteNum_][1].replaceAll("/+","")),"");
		
		searchingFor_ = (searchingFor_.replaceAll(websiteList[websiteNum_][1].replaceAll("/+",""),"")).replaceAll("\\"+websiteList[websiteNum_][2]," ");
		// searchingFor_ = searchingFor_.substring(1);
		
		// String searchingFor_ = "https://www.wikihow.com/wikiHowTo?search=test".replaceAll("https://www","!!!!!");
		//.replaceAll(websiteList[websiteNum_][2]," ");//Skip website code

		String clickableURL_ = (websiteToSeachThrough.substring(3).replaceAll("#@","?"))+websiteList[websiteNum_][3];//TODO: Change to include end format to website_//.replace('V','?');
		String searchingOnFormatted_ = websiteList[websiteNum_][1];
	//  https://www.wikihow.com/
		searchingOnFormatted_ = searchingOnFormatted_.substring(searchingOnFormatted_.indexOf("//")+2);
		if(searchingOnFormatted_.indexOf("/") != -1){
			searchingOnFormatted_ = searchingOnFormatted_.substring(0,searchingOnFormatted_.indexOf("/"));
		}

		boolean normalRun = true;
		if(normalRun){
			Card card = new Card(Card.TYPE_SINGLE);
			JSONObject linkCard = card.addElement(
					ElementType.link,
					JSON.make("title", "CustomWebSeach",
					 "desc","Searching for \""+searchingFor_+"\" on "+searchingOnFormatted_+""/*"</u><!--<br><--                          "+*/),//tmpStr_
					null, null, "", 
					clickableURL_,//URL of website with search
					"https://sepia-framework.github.io/img/icon.png", 
					null, null
			);
			JSON.put(linkCard, "imageBackground", "#000");
			api.addCard(card.getJSON());
		}

		//all good
		api.setStatusSuccess();
		
		//build the API_Result
		ServiceResult result = api.buildResult();
		return result;
	}
	
	//----------------- custom parameters -------------------
	
	public static class GetWebsiteParameter extends CustomParameter {

		private String formatStart(String input_){
			while(input_.contains("  ")){input_ = input_.replaceAll("  "," ");}
			input_ = input_.substring(7);//search.length (removes search & first space)
			input_ = input_.toLowerCase();//TODO: Allow for serching with bouthcase
			return input_;
		}

		@Override
		public String extract(String input_){
			int website_ = -1;
			input_ = formatStart(input_);

			// String returningString_ = "noReturnChange";//Nothing found
			// Locate which website is needed


			//search wikihow pineapple
			//wikihow pineapple

			for(int i = 0; i < websiteList.length; i++){
				if(input_.indexOf(websiteList[i][0])==0){//(websiteList[1][0].length())){//This checks structure
					website_ = i;
					i = websiteList[0].length;//Exit
				}
			}
			if(website_ == -1){
				website_ = 0;
			}
			input_ = input_.substring(websiteList[website_][0].length()+0);//Remove space after
			if(input_.charAt(0)==' '){input_ = input_.substring(1);}
			if(input_.charAt(0)==' '){input_ = input_.substring(1);}
			if(input_.indexOf("for")==0){input_ = input_.substring(3);input_+=""/*"-"*/;}//removes search search ___ "for "
			// input now is what we want to search
			if(input_.charAt(0)==' '){input_ = input_.substring(1);}
			if(input_.charAt(0)==' '){input_ = input_.substring(1);}

			input_ = input_.replaceAll(" ",websiteList[website_][2]);//Replace spaces for replacement
			input_ = websiteList[website_][1] + input_;

			// First 2 spaces = website number, everything after = search until /void after this ¬, everything after that is full website search
			String tmpStr_ = "00"+website_;
			tmpStr_ = tmpStr_.substring(tmpStr_.length()-3);

			return tmpStr_ + input_;
			//Other languages
			// }else{
			// 	Debugger.println("Custom parameter 'websiteToSeachThrough' does not support language: " + this.language, 1);
			// }
		}

		@Override
		public String responseTweaker(String input){
			if (language.equals(LANGUAGES.EN)){
				return input;
			}else{
				Debugger.println("Custom parameter 'websiteToSeachThrough' does not support language: " + this.language, 1);
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
				return "";
				// return "Error <user_data_unresolved>"; 		//TODO: this probably should become something like 'Interview.ERROR_USER_DATA_ACCESS' in the future;
			}else{
				//build result with entry for field "VALUE"
				JSONObject itemResultJSON = JSON.make(InterviewData.VALUE, input);
				this.buildSuccess = true;
				return itemResultJSON.toJSONString();
			}
		}		
	}

}
