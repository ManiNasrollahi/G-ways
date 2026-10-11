(function (window, document) {
  'use strict';
  function refresh() {
    var methods = document.getElementById('retryDialogForm:methods');
    var boxes = methods ? methods.querySelectorAll('input[type="checkbox"]') : [];
    var selected = 0;
    boxes.forEach(function (box) { if (box.checked) selected++; });
    var hint = document.getElementById('retryMethodHint');
    if (hint && boxes.length) { hint.textContent = selected ? selected + ' method' + (selected === 1 ? '' : 's') + ' selected' : 'Choose at least one method.'; hint.classList.toggle('is-warning', selected === 0); }
  }
  window.RetryPage = { refresh: refresh };
  document.addEventListener('DOMContentLoaded', refresh);
}(window, document));
