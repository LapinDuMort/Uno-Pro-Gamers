// card-values.js
// Converts UNO card enum values into display-friendly symbols/numbers

const valueMap = {
    "ZERO": 0,
    "ONE": 1,
    "TWO": 2,
    "THREE": 3,
    "FOUR": 4,
    "FIVE": 5,
    "SIX": 6,
    "SEVEN": 7,
    "EIGHT": 8,
    "NINE": 9,
    "SKIP": "⏭",
    "REVERSE": "⟳",
    "DRAWTWO": "+2",
    "WILD": "★",
    "WILDFOUR": "★+4"
};


function convertCardValues(containerSelector) {
    document.querySelectorAll(containerSelector).forEach(el => {
        const text = el.textContent.trim().toUpperCase();

        if (text in valueMap) {
            el.textContent = valueMap[text];
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    convertCardValues('.hand .uno-card .value');
    convertCardValues('.pile .uno-card.single .value');
});
