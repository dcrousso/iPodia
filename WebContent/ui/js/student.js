(function() {
	"use strict";

	Array.prototype.forEach.call(document.querySelectorAll("section > h4"), function(item) {
		item.addEventListener("click", function(event) {
			item.parentNode.classList.toggle("open");
			item.lastElementChild.classList.toggle("up");
		});
	});

	Array.prototype.forEach.call(document.querySelectorAll("form[method=\"post\"] > .item > .answer > p"), function(item) {
		item.addEventListener("click", function(event) {
			item.previousElementSibling.click();
		});
	});
})();
