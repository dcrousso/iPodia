(function() {
	"use strict";

	function addFileUpload(event) {
		if (this.nextElementSibling) {
			if (!this.value)
				this.remove();
			return;
		}

		var input = document.createElement("input");
		input.type = "file";
		input.name = "upload";
		input.addEventListener("change", addFileUpload);
		this.parentElement.appendChild(input);
	}
	Array.prototype.forEach.call(document.querySelectorAll("input[type=\"file\"]"), function(item) {
		item.addEventListener("change", addFileUpload);
	});

	function addQuestion(event) {
		var topic = this.parentNode;
		var inputs = topic.querySelectorAll("input, textarea");
		for (var i = 0; i < inputs.length; ++i) {
			if (!inputs[i].value || !inputs[i].value.length || !inputs[i].value.trim().length) {
				inputs[i].focus();
				return;
			}
		}

		var questionName = "Week" + parameters.num + topic.id + "Question";
		var lastQuestion = topic.lastElementChild.previousElementSibling;
		if (!lastQuestion || !lastQuestion.firstElementChild || !lastQuestion.firstElementChild.name)
			questionName += "1";
		else {
			var match = lastQuestion.firstElementChild.name.match(/\d+$/);
			questionName += match ? parseInt(match[0]) + 1 : "1";
		}

		var container = document.createElement("div");
		container.classList.add("item");

		var question = container.appendChild(document.createElement("textarea"));
		question.classList.add("question");
		question.placeholder = "Question";
		question.name = questionName;
		question.required = true;

		var answerOptions = ["A", "B", "C", "D", "E"];
		for (var i = 0; i < answerOptions.length; ++i) {
			var answer = container.appendChild(document.createElement("input"));
			answer.classList.add("answer");
			answer.placeholder = "Answer " + answerOptions[i];
			answer.name = questionName + "Answer" + answerOptions[i];
			answer.required = true;
		}

		var correctAnswerContainer = container.appendChild(document.createElement("div"));
		correctAnswerContainer.classList.add("correct");
		for (var i = 0; i < answerOptions.length; ++i) {
			var label = correctAnswerContainer.appendChild(document.createElement("label"));
			label.textContent = answerOptions[i];

			var radioButton = correctAnswerContainer.appendChild(document.createElement("input"));
			radioButton.type = "radio";
			radioButton.name = questionName + "CorrectAnswer";
			radioButton.value = answerOptions[i];
			radioButton.required = true;
		}

		topic.insertBefore(container, topic.lastElementChild);

		var top = getScrollTop() + container.getBoundingClientRect().top;
		if (top + container.offsetHeight < getScrollTop() || top + question.offsetHeight > getScrollTop() + window.innerHeight) {
			animateScroll(top, 750, function() {
				question.focus();
			});
		} else
			question.focus();
	}
	Array.prototype.forEach.call(document.querySelectorAll("button.add-question"), function(item) {
		item.addEventListener("click", addQuestion);
	});

	document.querySelector("button.match").addEventListener("click", function() {
		var type = this.classList.contains("in-class") ? "inClassMatching" : "beforeClassMatching";
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState != 4 || xhr.status != 200)
				return;

			alert("Students have been matched");
		};
		xhr.open("POST", "/admin/matching?type=" + type + "&id=" + parameters.id + "&num=" + parameters.num, true);
		xhr.send();
	});

	var inClassQuestions = document.querySelector(".in-class-questions");
	document.querySelector("button.add-in-class-question").addEventListener("click", function() {
		inClassQuestions.hidden = false;
		addQuestion.call(inClassQuestions.lastElementChild.lastElementChild);
	});
})();
