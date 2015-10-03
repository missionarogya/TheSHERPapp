/* Variables that will be used during the lifecyce */
var interiew_questions = null;
var initval_radio = -99;
var initval_text = '"#"';
var ans = [];
var current_question = null;
var arr_join = "~";
var j_dir = "json/";
var img_dir = "images/";
//var aud_dir = "/audio/";
var flist = null;
var load_init_ctr = null;
var timer;
var listfile;
var intervieweeID ;
//var ansRadio = document.getElementById("answer");
var nextButton = document.getElementById("btnNext");
var soundButton = document.getElementById("playQues");
var firstQuestion;

/* Start point */
function init(listfile){
	/* read list.json */
	var oReq = new XMLHttpRequest(); oReq.onload = filelist;
	//alert(oReq);
	oReq.open("get", j_dir+listfile, true); oReq.send();
	//alert("sent");
}
/* process each file specfied in list.json */
function filelist() {
	flist = JSON.parse(this.responseText);
	if(flist!=undefined && flist!='undefined' && flist!=null ) {
		var ctr = flist.filelist.length;
		load_init_ctr = ctr;
		initLoading();
		document.getElementById("surveyTotal").innerHTML = ctr;
		for(i=0; i < ctr; i++) {
			var oReq = new XMLHttpRequest(); oReq.onload = fetchQuestionList;
			oReq.open("get", j_dir+ flist.filelist[i], true);
			oReq.send();
		}
	}
}
/* set timer to check if the loading of the survey(s) has been completed */
function initLoading() {
	timer = setTimeout(function() { checkquestionsloaded(); }, 540);
}
/* All survey questions are now loaded */
function checkquestionsloaded(){
	if(load_init_ctr!=null && load_init_ctr == 0) {
		clearTimeout(timer);
		/* show the question based on the start point defined */
		firstQuestion = flist.start_question;
		generate_question(firstQuestion);
		document.getElementById('loading').className = "hide";
		document.getElementById('qDiv').className ='show';	
	}
}
function fetchQuestionList() { 
	parsedQuestion = JSON.parse(this.responseText);
	if(interiew_questions==undefined || interiew_questions=='undefined' || interiew_questions==null ) {
		interiew_questions = new Array();
	}
	if(parsedQuestion!=undefined && parsedQuestion!='undefined' && parsedQuestion!=null ) {
		/*show progress bar*/
		document.getElementById("currentSurvey").innerHTML = (flist.filelist.length - load_init_ctr);
		var wd = Math.round(window.screen.width * ((flist.filelist.length - load_init_ctr) / flist.filelist.length), 0); 
		document.getElementById("loadingpercent").style.width = wd + "px";
		/*show progress bar*/
		/* initialize the answers for each question */
		for(i=0; i < parsedQuestion.length; i++) {
			var question = parsedQuestion[i];
			interiew_questions.push(question);
			var initval = null;
			if(question.answers[0].type == "radio") {
				initval = initval_radio;
			} else {
				initval = initval_text;
			}
			eval("var " + question.questionid + "=" + initval + ";");
			window[question.questionid] = initval;			
		}		
	} 
	load_init_ctr--;
	return true;
}
/* Display the question to the end-user after processing the json */
function generate_question(questionid) {
	current_question = interiew_questions.filter(function (el) { return el.questionid == questionid; });
	if(current_question[0].isvisible == true) { /* general question to be displayed to the user */
	var audio_id = current_question[0].audio;	
	if(audio_id != null){
		generateAudioButtonName(audio_id);
		if(current_question[0].questionid == firstQuestion){        	
        	nextButton.style.display = 'inline';
			//ansRadio.style.display = 'inline';
        }
        else{
        	nextButton.style.display = 'none';
			//ansRadio.style.display = 'none';
			soundButton.style.display = 'none'
        }
	}
		/* set question */
		var q = document.getElementById('question');
		/* remove existing elements, if any */
		while (q.hasChildNodes()) { q.removeChild(q.lastChild); }

		var q_eng = current_question[0].question.en;
		var q_lang = current_question[0].question.lang;
		var qid = document.createElement('dt');
		
 		var id = current_question[0].questionid; id = id.replace("q_", "").replace(/_/g, ""); qid.innerHTML = id; q.appendChild(qid);
		var eng_question = document.createElement('dd'); eng_question.innerHTML = q_eng; q.appendChild( eng_question);
		qid = document.createElement('dt'); qid.innerHTML = "&nbsp;"; q.appendChild(qid);
		if(q_lang != undefined && q_lang != 'undefined' && q_lang != null) {
			var lang_question = document.createElement('dd'); lang_question.innerHTML = q_lang; q.appendChild( lang_question);
		}
		/* /set question */

		/* set answer */
		var a = document.getElementById('answer');
		while (a.hasChildNodes()) { a.removeChild(a.lastChild); }
		var ansList = current_question[0].answers;
		for(i=0; i < ansList.length; i++) {
			var en_ans = ansList[i].label.en;
			if(en_ans == undefined || en_ans == 'undefined' || en_ans == null) {
				en_ans = "";
			}
			var lang_ans = ansList[i].label.lang;
			if(lang_ans == undefined || lang_ans == 'undefined' || lang_ans == null) {
				lang_ans = "";
			}
			var lbl = document.createElement("label");
			lbl.innerHTML = "&nbsp;&nbsp;" + en_ans + "&nbsp;&nbsp;" + lang_ans+"<br/><br/><br/><br/><br/>";
			if(ansList[i].type == "radio" || ansList[i].type == "checkbox" || ansList[i].type == "text" || ansList[i].type == "number") {
			
				var rad = document.createElement('input'); 
				rad.setAttribute('type', ansList[i].type); 
				if(ansList[i].type == "number") {
					rad.setAttribute('step', "any");
					rad.setAttribute('pattern',"\d+"); 
				}
				rad.setAttribute('name', current_question[0].questionid); 
				rad.setAttribute('value', ansList[i].value);
				var ansDD = document.createElement('dd');
				ansDD.appendChild(rad);
				var image = ansList[i].image
				if(ansList[i].type == "radio" &&  image != null){
					var img = document.createElement('img'); 
					img.setAttribute('src', img_dir + image + ".png" );
					img.setAttribute('class',"col");
					ansDD.appendChild(img);					
				}								
				ansDD.appendChild(lbl); 
				a.appendChild(ansDD);	
			} else if(ansList[i].type == "textarea") {
				var rad = document.createElement('textarea'); 
				rad.setAttribute('name', current_question[0].questionid);
				rad.setAttribute('rows', 9);
				var ansDD = document.createElement('dd');
				ansDD.appendChild(rad);
				a.appendChild(ansDD);
			}
		}
		/* /set answer */
	} else {
		/* decision logic; isvisible = false */
		processDecisionLogic();
	}
}
function processQuestion() {
	console.log(current_question[0]);
	if(current_question[0].answers[0].type == "comment") {
		/* skip to next question after showing the comment */
		if(current_question[0].branchlogic.success == null || current_question[0].branchlogic.failure == null) {
			generate_final_result();
		}
		 else { 
			generate_question(current_question[0].branchlogic.success);					
		}			
	} else {	
		var val = null;
		var id = current_question[0].questionid;
		var qType = current_question[0].answers[0].type.toLowerCase();
		var objs = document.getElementsByName(id);
		texts = new Array(); 
		checks = new Array(); 
		/* get the values inputed by user based on the question type */
		switch(qType) {
			case  "radio":  
				for (i=0; i < objs.length; i++) {
					if(objs[i].checked) { 
						val = objs[i].value;
						break;
					}
				}
				break;
			case "text":
			case "number":		
				for (i=0; i < objs.length; i++) {
					if(objs[i].value!=undefined && objs[i].value!=null && objs[i].value!="" ) { 
						texts.push( objs[i].value );
					}
				}
				if(texts.length > 0) {
					val = texts.join(arr_join);
				}
				
				break;
			case "textarea":
				val = document.getElementsByTagName('textarea')[0].value
				break;
			case "checkbox":
				for (i=0; i < objs.length; i++) {
					if(objs[i].checked) { 
						checks.push( objs[i].value );
					}
				}
				if(checks.length > 0) {
					val = checks.join(arr_join);
				}
				break;
		}
		if(val !=undefined && val!='undefined' && val!=null && val!="" ) {
			/* get the value & branch to a question based on the decision logic) */
			if(qType != 'radio' && qType !== 'number') {alert("6");
				eval (id + "='" + val + "';");
			} else { 
				eval (id + "=" + val + ";");
			}
			/*	window[id] = val; */
			var logic = eval(current_question[0].branchlogic.logic);			
			if( logic == true || logic == "1" ) {
				if(current_question[0].branchlogic.success != null) {
					generate_question(current_question[0].branchlogic.success);
					goToNextQuestion(current_question[0].audioLength, current_question[0].audio);
				}  else {
					generate_final_result();			
				}
			} else {
				if(current_question[0].branchlogic.failure!=null) {
					generate_question(current_question[0].branchlogic.failure);
					goToNextQuestion(current_question[0].audioLength, current_question[0].audio);			
					}  else {
						generate_final_result();
					}
				}
			} else {
				JSBridgeToSaveAnswers.showAlert("Please select an answer before proceeding !");
			}
	}
}

function goToNextQuestion(audioLength, audio_id){
	if(audio_id != null){
		playQuestion(audio_id);
	}
	var counter = parseInt(audioLength) ;
	var id = setInterval(function() {					
		counter--;
		if(counter == 0) {
			nextButton.style.display = 'inline';
			//ansRadio.style.display = 'inline';
			soundButton.style.display = 'inline';
			clearInterval(id);
		}
		else{
			nextButton.style.display = 'none';
			//ansRadio.style.display = 'none';
			soundButton.style.display = 'none';
		}
	}, 1000);	
}

/*Process the branch logic for decision question; isvisible = false;*/
function processDecisionLogic() {
	var question = current_question[0];
	var qType = question.answers[0].type;
	var bLogic = question.branchlogic.logic;
	var val = eval(bLogic);
	eval(question.questionid + "=" + val +";");
	/* window[question.questionid] = val; */
	if(val == true || val == "1") { /* zero being treated as false, always */
		if(question.branchlogic.success!=null) {
			generate_question(question.branchlogic.success);
		} else {
			generate_final_result();
		}
	} else {
		if(question.branchlogic.failure!=null) { 
			generate_question(question.branchlogic.failure);
		} else {
			generate_final_result();
		}
	}
}

/*Process after all questions have been processed; when succes/failure is null*/
function generate_final_result() {
	document.getElementById('qDiv').className = "hide";
	/* show answers provided by the enduser in a list*/
	var ansList = document.getElementById('finalList');
	ansList.className = "show";

	for(i=0; i < interiew_questions.length; i++) {

		var question = interiew_questions[i];
		var r = document.createElement('li');
		var id = question.questionid; 
		var type = question.answers[0].type;
		var answer = window[question.questionid];
		/*var txt = id + " : " + answer + "&nbsp;&nbsp;<small>&nbsp;Type:" + type + "</small>&nbsp;&nbsp;<small>&nbsp;Visible:" + question.isvisible + "</small>";*/
		var txt = id + " :: " + answer;
		r.innerHTML = txt;
		//ansList.appendChild(r);
		if (answer != -99 && answer != "#"){
			ans.push({"question": id, "answer" : answer});
			}
		}
	JSBridgeToSaveAnswers.saveAnswersToApp(JSON.stringify(ans));
}

function generateAudioButtonName(audioId){
	var audioButton = document.getElementById('playQues');
	audioButton.name = audioId.toString();
}

function playQuestion(audioName){
	var audiosrc = audioName.toString()+".mp3";
	JSBridgeToSaveAnswers.playMusic(audiosrc);
}