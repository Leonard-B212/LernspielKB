/**
 * Enthält die HTTP-Kommunikation mit der Level-Verifikation.
 *
 * Der Client sendet nur die Level-ID und den tatsächlich erzeugten
 * ExecutionLog. Der erwartete Log bleibt ausschließlich im Backend.
 */

// Prüft den tatsächlichen ExecutionLog gegen die Backend-Vorgaben des Levels.
export async function verifyLevel(levelID, actualExecutionLog) {
    const response = await fetch("/api/levelVerification/verify", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            levelID,
            actualExecutionLog
        })
    });

    if (!response.ok) {
        const errorText = await response.text();

        throw new Error(
            errorText || `Verifikationsfehler: HTTP ${response.status}`
        );
    }

    return response.json();
}