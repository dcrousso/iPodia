(function() {
	"use strict";

	// DOM queries
	var header = document.querySelector("header");
	var scrollTop = document.querySelector(".scroll-top");
	
	// Helper functions
	function animate(duration, action, onComplete) {
		var animationFrame = window.requestAnimationFrame
		                  || window.webkitRequestAnimationFrame
		                  || window.mozRequestAnimationFrame
		                  || window.oRequestAnimationFrame
		                  || window.msRequestAnimationFrame
		                  || function(callback) {
		                     	window.setTimeout(callback, 1000 / 60);
		                     };
		var start = null;
		function step(timestamp) {
			if (!start)
				start = timestamp;
			var progress = timestamp - start;
			if (typeof action === "function")
				action(progress);
			if (progress < duration)
				animationFrame(step);
			else if (typeof onComplete === "function")
				onComplete();
		}
		animationFrame(step);
	}

	function getScrollTop() {
		if (window.pageYOffset !== undefined)
			return window.pageYOffset;
		return (document.documentElement || document.body.parentNode || document.body).scrollTop;
	}

	function easeInOutQuad(progress, start, delta, duration) {
		progress /= (duration / 2);
		if (progress < 1)
			return ((progress * progress * (delta / 2)) + start);
		--progress;
		return ((((progress * (progress - 2)) - 1) * (-delta / 2)) + start);
	}

	function animateScrollToTop(duration) {
		animate(duration, function(progress) {
			var scrollTop = getScrollTop();
			var top = easeInOutQuad(progress, scrollTop, -scrollTop, duration);
			window.scroll(0, top);
		});
	}

	scrollTop.addEventListener("click", function() {
		animateScrollToTop(750);
	});
	
	// Scroll
	function checkHeight() {
		var scrolled = (getScrollTop() > 0);
		scrollTop.classList.toggle("scrolled", scrolled);
		header.classList.toggle("scrolled", scrolled);
	}
	checkHeight();
	window.addEventListener("scroll", checkHeight);
})();
