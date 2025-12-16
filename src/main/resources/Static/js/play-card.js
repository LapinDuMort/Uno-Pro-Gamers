function playCard(button) {
    const index = button.dataset.index;
    const colour = button.dataset.colour;

    if (colour.toUpperCase() === 'WILD') {
        window.tempCardIndex = index;
        document.getElementById("colorPicker").style.display = "block";
        return;
    }

    const form = document.getElementById("playForm" + index);
    if (form) form.submit();
}

function chooseColor(color) {
    document.getElementById("colorPicker").style.display = "none";

    const indexInput = document.getElementById("wildCardIndex");
    const colorInput = document.getElementById("wildColor");

    if (!indexInput || !colorInput) {
        console.error("Hidden form inputs not found!");
        return;
    }

    indexInput.value = window.tempCardIndex;
    colorInput.value = color;

    document.getElementById("wildForm").submit();
}
