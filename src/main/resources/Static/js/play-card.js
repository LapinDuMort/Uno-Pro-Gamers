// play-card.js
document.addEventListener('DOMContentLoaded', () => {
  const handButtons = document.querySelectorAll('#hand-container form button.uno-card');

  handButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      // ignore unplayable cards
      if (btn.dataset.playable === 'false') return;

      const colour = btn.dataset.colour;
      const index = btn.dataset.index;

      if (colour && colour.toLowerCase() === 'wild') {
        const picker = document.getElementById('colorPicker');
        const wildIndexInput = document.getElementById('wildCardIndex');
        if (picker && wildIndexInput) {
          wildIndexInput.value = index;
          picker.style.display = 'block';
        }
      } else {
        const form = btn.closest('form');
        if (form) form.submit();
      }
    });
  });

  // Called by the color buttons in the popup
  window.chooseColor = function(chosen) {
    const wildIndexInput = document.getElementById('wildCardIndex');
    const wildColorInput = document.getElementById('wildColor');
    const wildForm = document.getElementById('wildForm');
    const picker = document.getElementById('colorPicker');

    if (!wildForm || !wildColorInput || !wildIndexInput) return;

    wildColorInput.value = chosen;
    wildForm.submit();

    if (picker) picker.style.display = 'none';
  };

  // UX: close popup on Escape or click outside
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
      const picker = document.getElementById('colorPicker');
      if (picker) picker.style.display = 'none';
    }
  });

  document.addEventListener('click', e => {
    const picker = document.getElementById('colorPicker');
    if (!picker || picker.style.display !== 'block') return;
    if (!picker.contains(e.target) && !e.target.closest('.uno-card')) {
      picker.style.display = 'none';
    }
  });
});
