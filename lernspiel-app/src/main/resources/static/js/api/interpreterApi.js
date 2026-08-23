/**
 * Enthält die HTTP-Kommunikation mit dem Interpreter-Backend.
 *
 * Die Sandbox bzw. spätere Level-Seiten müssen dadurch nicht wissen,
 * wie der konkrete REST-Aufruf aufgebaut ist.
 *
 * Später können weitere Interpreter- oder Level-Endpunkte
 * in diesem API-Bereich ergänzt werden.
 */

// Sendet einen ProgramRequest an den Interpreter und liefert dessen Ausgabe als String-Liste zurück.
export async function runProgram(programRequest) {
    const response = await fetch("/game/interpreter/run", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(programRequest)
    });

    if (!response.ok) {
        const errorText = await response.text();

        throw new Error(
            errorText || `Interpreter-Fehler: HTTP ${response.status}`
        );
    }

    return response.json();
}