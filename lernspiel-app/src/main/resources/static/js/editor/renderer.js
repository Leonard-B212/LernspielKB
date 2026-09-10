/**
 * Rendert den aktuellen Editor-State als sichtbare Codezeilen.
 *
 * Der Renderer kennt die Blockdaten, verändert sie aber nicht.
 * Er erhält das aktuelle program-Array und erzeugt daraus das DOM.
 *
 * Drag-&-Drop-Events einzelner Programmblöcke werden über Callbacks
 * an den DragDropController weitergereicht.
 */

import { getBlockCssClass, getBlockLabel } from "./blockDefinitions.js";

export function createProgramRenderer({
    programDropzone,
    onBlockDragStart,
    onBlockDragEnd
}) {
    function render(program) {
        renderLines(programDropzone, program, []);
    }

    // Rendert einen BREAK-/if-else-Ketten-gruppierten Container (Wurzel-Programm oder if/else-Body).
    function renderLines(container, program, path) {
        container.innerHTML = "";
        container.dataset.containerPath = JSON.stringify(path);
        container.dataset.length = program.length;

        buildProgramLines(program).forEach((lineData, lineIndex) => {
            container.appendChild(createCodeLine(lineData, lineIndex, path));
        });
    }

    // Rendert eine flache, nicht in Zeilen unterteilte Bedingung (kein BREAK darin möglich).
    function renderInlineExpression(container, expression, path) {
        container.innerHTML = "";
        container.dataset.containerPath = JSON.stringify(path);
        container.dataset.length = expression.length;

        container.appendChild(createDropIndicator(path, 0));

        expression.forEach((block, index) => {
            container.appendChild(createLeafBlock(block, index, path));
            container.appendChild(createDropIndicator(path, index + 1));
        });

        if (expression.length === 0) {
            const hint = document.createElement("span");
            hint.classList.add("empty-expression-hint");
            hint.textContent = "Bedingung";
            container.appendChild(hint);
        }
    }

    function createCodeLine(lineData, lineIndex, path) {
    const line = document.createElement("div");
    line.classList.add("code-line");
    line.dataset.containerPath = JSON.stringify(path);
    line.dataset.endIndex = lineData.endIndex;

    const lineNumber = document.createElement("div");
    lineNumber.classList.add("line-number");
    lineNumber.textContent = lineIndex + 1;
    line.appendChild(lineNumber);

    const content = document.createElement("div");
    content.classList.add("code-line-content");
    content.appendChild(createDropIndicator(path, lineData.startIndex));

    if (lineData.blocks.length > 0 && lineData.blocks[0].block.type === "IF_STATEMENT") {
        content.appendChild(createConditionalChain(lineData.blocks, path));
        content.appendChild(createDropIndicator(path, lineData.endIndex));
    } else {
        lineData.blocks.forEach(({ block, index }) => {
            content.appendChild(createLeafBlock(block, index, path));
            content.appendChild(createDropIndicator(path, index + 1));
        });
    }

    if (lineData.blocks.length === 0) {
        const hint = document.createElement("span");
        hint.classList.add("empty-line-hint");
        hint.textContent = lineIndex === 0 ? "Ziehe Code-Blöcke hier hinein" : "Nächste Codezeile";
        content.appendChild(hint);
    }

    line.appendChild(content);
    return line;
}

// Rendert eine vollständige if -> else-if -> ... -> else Kette als EIN zusammenhängendes
// Java-artiges Element, statt als mehrere separat gestapelte Blöcke.
function createConditionalChain(blocksWithIndex, path) {
    const wrapper = document.createElement("div");
    wrapper.classList.add("code-block", "program-block", "block-control", "conditional-chain");
    wrapper.dataset.containerPath = JSON.stringify(path);
    wrapper.dataset.index = blocksWithIndex[0].index;
    wrapper.draggable = true;

    const spanCount = blocksWithIndex.length;
    bindDrag(wrapper, path, blocksWithIndex[0].index, spanCount);

    let i = 0;
    while (i < blocksWithIndex.length) {
        const { block, index } = blocksWithIndex[i];
        const isFirst = i === 0;

        if (block.type === "IF_STATEMENT") {
            wrapper.appendChild(createConditionalSegment({
                keyword: isFirst ? "if" : "else if",
                expression: block.expression,
                expressionPath: [...path, { index, field: "expression" }],
                body: block.program,
                bodyPath: [...path, { index, field: "program" }],
                isFirst
            }));
        } else if (block.type === "ELSE_STATEMENT") {
            // Nur rendern, wenn es KEIN reiner Kettenmarker vor einem folgenden IF_STATEMENT
            // ist - dessen eigenes .program wird vom Interpreter ignoriert (siehe oben).
            const isChainMarker = i + 1 < blocksWithIndex.length && blocksWithIndex[i + 1].block.type === "IF_STATEMENT";
            if (!isChainMarker) {
                wrapper.appendChild(createConditionalSegment({
                    keyword: "else",
                    body: block.program,
                    bodyPath: [...path, { index, field: "program" }],
                    isFirst
                }));
            }
        }
        i++;
    }

    const closingBrace = document.createElement("div");
    closingBrace.classList.add("control-closing-brace");
    closingBrace.textContent = "}";
    wrapper.appendChild(closingBrace);

    return wrapper;
}

function createConditionalSegment({ keyword, expression, expressionPath, body, bodyPath, isFirst }) {
    const segment = document.createElement("div");
    segment.classList.add("conditional-segment");

    const header = document.createElement("div");
    header.classList.add("control-header");

    if (!isFirst) {
        const closeBrace = document.createElement("span");
        closeBrace.classList.add("control-syntax");
        closeBrace.textContent = "}";
        header.appendChild(closeBrace);
    }

    const keywordSpan = document.createElement("span");
    keywordSpan.classList.add("control-keyword");
    keywordSpan.textContent = keyword;
    header.appendChild(keywordSpan);

    if (expression) {
        const openParen = document.createElement("span");
        openParen.classList.add("control-syntax");
        openParen.textContent = "(";

        const expressionZone = document.createElement("div");
        expressionZone.classList.add("expression-dropzone");

        const closeParen = document.createElement("span");
        closeParen.classList.add("control-syntax");
        closeParen.textContent = ")";

        header.append(openParen, expressionZone, closeParen);
        renderInlineExpression(expressionZone, expression, expressionPath);
    }

    const openBrace = document.createElement("span");
    openBrace.classList.add("control-syntax");
    openBrace.textContent = "{";
    header.appendChild(openBrace);

    segment.appendChild(header);

    const bodyZone = document.createElement("div");
    bodyZone.classList.add("control-body");
    segment.appendChild(bodyZone);
    renderLines(bodyZone, body, bodyPath);

    return segment;
}

    function createLeafBlock(blockData, index, path) {
        const element = document.createElement("div");

        element.classList.add("code-block", "program-block", getBlockCssClass(blockData.type));
        element.dataset.containerPath = JSON.stringify(path);
        element.dataset.index = index;
        element.draggable = true;
        element.textContent = getBlockLabel(blockData);

        bindDrag(element, path, index);
        return element;
    }

    function bindDrag(element, path, index, spanCount = 1) {
        element.addEventListener("dragstart", (event) => {
            event.stopPropagation();
            onBlockDragStart(event, element, path, index, spanCount);
        });
        element.addEventListener("dragend", () => onBlockDragEnd(element));
    }

    function createDropIndicator(path, insertIndex) {
        const indicator = document.createElement("div");
        indicator.classList.add("drop-indicator");
        indicator.dataset.containerPath = JSON.stringify(path);
        indicator.dataset.insertIndex = insertIndex;
        return indicator;
    }

    return { render };
}

/**
 * Teilt ein flaches Programm-Array in sichtbare Zeilen. Eine Zeile endet entweder bei einem
 * BREAK-Block oder - für if/else-Ketten - sobald die Kette (if -> else-if -> ... -> else)
 * vollständig ist, ganz ohne abschließendes BREAK. Muss synchron zu
 * InterpreterService.parseCode() im Backend gehalten werden.
 */
function buildProgramLines(program) {
    const lines = [];
    let index = 0;

    while (index < program.length) {
        const lineStart = index;

        if (program[index].type === "IF_STATEMENT") {
            index = findConditionalChainEnd(program, index);
        } else {
            index = scanRegularLine(program, index);
        }

        lines.push(toLineData(program, lineStart, index));
    }

    if (program.length === 0 || isLineComplete(program[program.length - 1])) {
        lines.push({ startIndex: program.length, endIndex: program.length, blocks: [] });
    }

    return lines;
}

/**
 * Scannt eine "normale" Zeile vorwärts, bis entweder ein BREAK gefunden wird (gehört noch
 * zur Zeile) oder ein IF_STATEMENT den Beginn einer neuen Zeile markiert - die aktuelle Zeile
 * bleibt dann bewusst unvollständig/ohne Semikolon, exakt wie im Backend-Parser (dort wird an
 * dieser Stelle geworfen; hier im Live-Editor lassen wir die Zeile stattdessen einfach offen,
 * damit während des Bauens nichts abstürzt). Ein einzelner, nicht zu einer Kette gehörender
 * ELSE_STATEMENT-Block (z. B. durch einen fehlerhaften Zug isoliert) wird als eigene Zeile
 * behandelt, damit der Scan garantiert vorankommt und es nicht zur Endlosschleife kommt.
 */
function scanRegularLine(program, start) {
    if (program[start].type === "ELSE_STATEMENT") {
        return start + 1;
    }

    let index = start;
    while (index < program.length
        && program[index].type !== "BREAK"
        && program[index].type !== "IF_STATEMENT"
        && program[index].type !== "ELSE_STATEMENT") {
        index++;
    }

    if (index < program.length && program[index].type === "BREAK") {
        index++;
    }

    return index;
}

function findConditionalChainEnd(program, startIndex) {
    let index = startIndex + 1;

    while (index < program.length && program[index].type === "ELSE_STATEMENT") {
        const afterElse = index + 1;
        if (afterElse < program.length && program[afterElse].type === "IF_STATEMENT") {
            index = afterElse + 1;
        } else {
            index = afterElse;
            break;
        }
    }

    return index;
}

function isLineComplete(lastBlock) {
    return lastBlock.type === "BREAK" || lastBlock.type === "IF_STATEMENT" || lastBlock.type === "ELSE_STATEMENT";
}

function toLineData(program, startIndex, endIndex) {
    const blocks = [];
    for (let i = startIndex; i < endIndex; i++) {
        blocks.push({ block: program[i], index: i });
    }
    return { startIndex, endIndex, blocks };
}