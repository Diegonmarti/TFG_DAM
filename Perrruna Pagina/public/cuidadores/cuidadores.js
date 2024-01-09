const darkModeToggle = document.querySelector('#dark-mode-toggle');

darkModeToggle.addEventListener('change', () => {
  document.body.classList.toggle('dark-mode');
});
