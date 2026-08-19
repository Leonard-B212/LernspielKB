/**
 * Optionale Bewegungsschicht für den Skilltree.
 *
 * Die Nodes behalten ihre vom Renderer vorgegebenen Grundpositionen.
 * Diese Physics-Schicht erzeugt lediglich kleine visuelle Abweichungen:
 *
 * - kurzes Einpendeln beim Laden
 * - leichte Abstoßung zwischen benachbarten Level-Nodes
 * - Rückbewegung zur ursprünglichen Position
 *
 * Die eigentliche Struktur des Skilltrees hängt nicht von dieser Datei ab.
 * 
 * Oben sind Konstanten definiert, die die Stärke der Kräfte und die Dauer der Simulation steuern.
 */

const SIMULATION_DURATION = 2800;

const REPULSION_DISTANCE = 135;
const REPULSION_STRENGTH = 0.018;

const DAMPING = 0.86;


/**
 * Startet eine kurze Physics-Simulation für die Level-Nodes.
 */
export function initializeSkilltreePhysics({
    skilltreeElement,
    onUpdate
}) {

    const nodeElements = [
        ...skilltreeElement.querySelectorAll(
            ".skilltree-level-node"
        )
    ];

    const categoryElements = [
        ...skilltreeElement.querySelectorAll(
            ".skilltree-category-node"
        )
    ];


    if (nodeElements.length === 0) {
        return;
    }


    const nodes =
        nodeElements.map(
            createPhysicsNode
        );


    const startTime =
        performance.now();


    function animate(currentTime) {

        const elapsed =
            currentTime - startTime;

        const progress =
            Math.min(
                elapsed / SIMULATION_DURATION,
                1
            );

        // Startet bei 1 und nähert sich langsam 0.
        const intensity =
            Math.pow(
                1 - progress,
                1.5
            );


        applyRepulsion(
            nodes,
            intensity
        );

        applyCategoryRepulsion(
            nodes,
            categoryElements,
            intensity
        );

        updateNodes(
            nodes
        );


        if (onUpdate) {
            onUpdate();
        }


        if (
            elapsed
            < SIMULATION_DURATION
        ) {

            requestAnimationFrame(
                animate
            );
        }
    }


        requestAnimationFrame(
            animate
        );
    }


/**
 * Erstellt den internen Physics-State eines Level-Nodes.
 */
function createPhysicsNode(element) {

    const baseX =
        Number.parseFloat(
            element.style.getPropertyValue(
                "--node-x"
            )
        ) || 0;

    const baseY =
        Number.parseFloat(
            element.style.getPropertyValue(
                "--node-y"
            )
        ) || 0;


    /*
     * Kleine zufällige Startabweichung erzeugt
     * das kurze Einpendeln beim Laden.
     */
    const offsetX =
        (Math.random() - 0.5) * 10;

    const offsetY =
        (Math.random() - 0.5) * 8;


    return {
        element,

        baseX,
        baseY,

        x:
            baseX + offsetX,

        y:
            baseY + offsetY,

        velocityX: 0,
        velocityY: 0
    };
}


/**
 * Nodes stoßen sich innerhalb eines bestimmten Radius leicht ab.
 */
function applyRepulsion(
    nodes,
    intensity) {

    for (
        let firstIndex = 0;
        firstIndex < nodes.length;
        firstIndex++
    ) {

        for (
            let secondIndex = firstIndex + 1;
            secondIndex < nodes.length;
            secondIndex++
        ) {

            const first =
                nodes[firstIndex];

            const second =
                nodes[secondIndex];


            const deltaX =
                second.x - first.x;

            const deltaY =
                second.y - first.y;


            const distance =
                Math.sqrt(
                    deltaX * deltaX
                    + deltaY * deltaY
                );


            if (
                distance === 0
                || distance
                    >= REPULSION_DISTANCE
            ) {
                continue;
            }


            const force =
                (
                    REPULSION_DISTANCE
                    - distance
                )
                * REPULSION_STRENGTH
                * intensity;


            const normalizedX =
                deltaX / distance;

            const normalizedY =
                deltaY / distance;


            first.velocityX -=
                normalizedX * force;

            first.velocityY -=
                normalizedY * force;


            second.velocityX +=
                normalizedX * force;

            second.velocityY +=
                normalizedY * force;
        }
    }
}

/**
 * Hält Level-Nodes auf Abstand zu den festen Kategorie-Nodes.
 *
 * Kategorien selbst bewegen sich nicht und wirken lediglich
 * als Hindernisse innerhalb der Physics-Simulation.
 */
function applyCategoryRepulsion(
    nodes,
    categoryElements,
    intensity) {

    const SAFE_DISTANCE = 90;
    const STRENGTH = 0.08;


    nodes.forEach(node => {

        const nodeRect =
            node.element.getBoundingClientRect();

        const nodeCenterX =
            nodeRect.left
            + nodeRect.width / 2;

        const nodeCenterY =
            nodeRect.top
            + nodeRect.height / 2;


        categoryElements.forEach(
            categoryElement => {

                const categoryRect =
                    categoryElement
                        .getBoundingClientRect();


                /*
                 * Nächster Punkt innerhalb der Kategorie-Box
                 * relativ zum Mittelpunkt des Level-Nodes.
                 */
                const closestX =
                    Math.max(
                        categoryRect.left,
                        Math.min(
                            nodeCenterX,
                            categoryRect.right
                        )
                    );

                const closestY =
                    Math.max(
                        categoryRect.top,
                        Math.min(
                            nodeCenterY,
                            categoryRect.bottom
                        )
                    );


                const deltaX =
                    nodeCenterX
                    - closestX;

                const deltaY =
                    nodeCenterY
                    - closestY;


                const distance =
                    Math.sqrt(
                        deltaX * deltaX
                        + deltaY * deltaY
                    );


                if (
                    distance >= SAFE_DISTANCE
                ) {
                    return;
                }


                /*
                 * Falls der Mittelpunkt genau innerhalb der Box liegt,
                 * wird der Node nach unten herausgedrückt.
                 */
                if (distance === 0) {

                    node.velocityY +=
                        STRENGTH
                        * SAFE_DISTANCE
                        * intensity;

                    return;
                }


                const force =
                    (
                        SAFE_DISTANCE
                        - distance
                    )
                    * STRENGTH
                    * intensity;


                node.velocityX +=
                    deltaX
                    / distance
                    * force;

                node.velocityY +=
                    deltaY
                    / distance
                    * force;
            }
        );
    });
}



/**
 * Aktualisiert Position und Geschwindigkeit der Nodes.
 */
function updateNodes(nodes) {

    nodes.forEach(node => {

        node.velocityX *=
            DAMPING;

        node.velocityY *=
            DAMPING;


        node.x +=
            node.velocityX;

        node.y +=
            node.velocityY;


        node.element.style.setProperty(
            "--node-x",
            `${node.x}px`
        );

        node.element.style.setProperty(
            "--node-y",
            `${node.y}px`
        );
    });
}