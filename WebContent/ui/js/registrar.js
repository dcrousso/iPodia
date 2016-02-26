(function() {
	"use strict";

	function getFirstEmptyInput(container) {
		for (var i = 0; i < container.children.length; ++i) {
			var child = container.children[i];
			if (child.nodeName === "INPUT" && !child.value)
				return child;
		}
		return null;
	}

	var teachers = document.getElementById("teachers");
	teachers.querySelector("button").addEventListener("click", function(event) {
		var input = getFirstEmptyInput(teachers);
		if (!input) {
			input = document.createElement("input");
			input.type = "text";
			input.name = "teacher";
			teachers.insertBefore(input, this);
		}
		input.focus();
	});

	var students = document.getElementById("students");
	students.querySelector("button").addEventListener("click", function(event) {
		var input = getFirstEmptyInput(students);
		if (!input) {
			input = document.createElement("input");
			input.type = "text";
			input.name = "student";
			students.insertBefore(input, this);
		}
		input.focus();
	});
})();
