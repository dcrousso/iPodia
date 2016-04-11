(function() {
	"use strict";

	var resultSection = document.querySelector("section.results");

	function addFileUpload(event) {
		if (this.files && this.files[0].size > 1024 * 1024 * 50) {
			alert("Error: file over 50MB");
			this.value = null;
		}

		if (this.nextElementSibling) {
			if (!this.value)
				this.remove();
			return;
		}

		if (!this.value)
			return;

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

	function createResponseTable(json, headerItems, createRow) {
		resultSection.textContent = "";

		var title = resultSection.appendChild(document.createElement("h4"));
		title.textContent = "Data";

		var table = resultSection.appendChild(document.createElement("table"));

		var headerRow = table.appendChild(document.createElement("tr"));

		for (var i = 0; i < headerItems.length; ++i) {
			var headerItem = headerRow.appendChild(document.createElement("th"));
			headerItem.textContent = headerItems[i];
		}

		for (var i = 0; i < json.length; ++i)
			createRow.call(table, json[i], i);

		var button = resultSection.appendChild(document.createElement("button"));
		button.textContent = "Dismiss";
		button.addEventListener("click", function(event) {
			resultSection.hidden = true;
		});

		resultSection.hidden = false;
	}

	var matchingElements = document.querySelectorAll("button.match");
	for (var i = 0; i < matchingElements.length; ++i) {
		matchingElements[i].addEventListener("click", function() {
			var type = this.classList.contains("in-class") ? "inClassMatching" : "beforeClassMatching";
			var algorithm = this.classList.contains("most-fair-algorithm") ? "mostFair": "recommendation";
			var xhr = new XMLHttpRequest();
			xhr.onreadystatechange = function() {
				if (xhr.readyState != 4 || xhr.status != 200)
					return;
	
				var response = JSON.parse(xhr.responseText);
				if (!resultSection) {
					console.log(response);
					alert("Error!  Missing result section!\nOpen console to see data.");
					return;
				}
	
				createResponseTable(response, ["Group", "Email", "Name"], function(item, index) {
					for (var key in item) {
						var row = this.appendChild(document.createElement("tr"));
	
						var groupNumber = row.appendChild(document.createElement("td"));
						groupNumber.textContent = index;
	
						var email = row.appendChild(document.createElement("td"));
						email.textContent = key;
	
						var name = row.appendChild(document.createElement("td"));
						name.textContent = item[key];
 
					}
				});
			};
			xhr.open("POST", "/admin/matching?type=" + type + "&algorithm=" + algorithm + "&id=" + parameters.id + "&num=" + parameters.num, true);
			xhr.send();
		});
	}
	
//	document.querySelector("button.generate-analytics").addEventListener("click", function() {
//	});
	
	
	
	
	document.querySelector("button.view-student-scores").addEventListener("click", function() {
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (xhr.readyState != 4 || xhr.status != 200)
				return;

			var response = JSON.parse(xhr.responseText);
			if (!resultSection) {
				console.log(response);
				alert("Error!  Missing result section!\nOpen console to see data.");
				return;
			}

			createResponseTable(response, ["Group", "Email", "Name", "1b", "2b", "3b", "4b", "1a", "2a", "3a", "4a"], function(item, index) {
				for (var key in item) {
					var row = this.appendChild(document.createElement("tr"));

					var groupNumber = row.appendChild(document.createElement("td"));
					groupNumber.textContent = index + 1;

					var email = row.appendChild(document.createElement("td"));
					email.textContent = key;

					var name = row.appendChild(document.createElement("td"));
					name.textContent = item[key].name;

					var topic1Before = row.appendChild(document.createElement("td"));
					topic1Before.textContent = item[key].topic1.before;

					var topic2Before = row.appendChild(document.createElement("td"));
					topic2Before.textContent = item[key].topic2.before;

					var topic3Before = row.appendChild(document.createElement("td"));
					topic3Before.textContent = item[key].topic3.before;

					var topic4Before = row.appendChild(document.createElement("td"));
					topic4Before.textContent = item[key].topic4.before;

					var topic1After = row.appendChild(document.createElement("td"));
					topic1After.textContent = item[key].topic1.after;

					var topic2After = row.appendChild(document.createElement("td"));
					topic2After.textContent = item[key].topic2.after;

					var topic3After = row.appendChild(document.createElement("td"));
					topic3After.textContent = item[key].topic3.after;

					var topic4After = row.appendChild(document.createElement("td"));
					topic4After.textContent = item[key].topic4.after;
				}
			});
		};
		xhr.open("POST", "/admin/analytics?id=" + parameters.id + "&num=" + parameters.num, true);
		xhr.send();
	});

	var inClassQuestions = document.querySelector(".in-class-questions");
	document.querySelector("button.add-in-class-question").addEventListener("click", function() {
		inClassQuestions.hidden = false;
		addQuestion.call(inClassQuestions.lastElementChild.lastElementChild);
	});
})();
