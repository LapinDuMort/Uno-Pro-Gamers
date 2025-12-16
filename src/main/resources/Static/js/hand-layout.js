window.onload = () => {
    const handContainer = document.getElementById('hand-container');
    const cardForms = handContainer.querySelectorAll('form');
    const cardCount = cardForms.length;

    if (cardCount === 0) return;

    const cardWidth = 110; // px
    const handWidth = handContainer.clientWidth;
    const maxSpacing = 50; // max spacing for large hands

    let spacing;

    if (cardCount === 1) {
        spacing = 0;
    } else {
        // Calculate the spacing needed to fill the container
        spacing = (handWidth - cardWidth) / (cardCount - 1);

        // If spacing is too large, cap it to maxSpacing
        const requiredWidth = cardWidth + spacing * (cardCount - 1);
        if (requiredWidth > handWidth) {
            spacing = (handWidth - cardWidth) / (cardCount - 1);
            spacing = Math.min(spacing, maxSpacing);
        }
    }

    // Total width of hand for centering
    const handTotalWidth = cardWidth + spacing * (cardCount - 1);
    const offsetLeft = (handWidth - handTotalWidth) / 2;

    // Position each card
    cardForms.forEach((form, index) => {
        const card = form.querySelector('.uno-card');
        if (!card) return;

        card.style.position = 'absolute';
        card.style.bottom = '0px';
        card.style.left = `${offsetLeft + index * spacing}px`;
        card.style.zIndex = index;

        // Set background color
        const colour = card.dataset.colour;
        card.style.backgroundColor = (colour && colour.toLowerCase() === 'wild') ? 'black' : colour;

        // Click lift
        card.addEventListener('click', () => {
            const isUp = card.style.transform === 'translateY(-50px)';
            card.style.transform = isUp ? 'translateY(0px)' : 'translateY(-50px)';
            card.style.zIndex = isUp ? index : 999;
        });
    });
};