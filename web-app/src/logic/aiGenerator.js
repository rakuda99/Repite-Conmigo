export const generateVerbDetails = async (apiKey, verb) => {
  if (!apiKey) {
    throw new Error("API Key is missing");
  }

  const prompt = `
Generate detailed information for the Spanish verb '${verb}' and return it ONLY as a valid JSON object.
Use the first-person singular (Yo - I) for all conjugations.

Required JSON Structure:
{
  "infinitive_meaning": "meaning of the infinitive in Arabic",
  "past": "preterite conjugation for 'Yo'",
  "past_meaning": "meaning of the past conjugation in Arabic",
  "present": "present conjugation for 'Yo'",
  "present_meaning": "meaning of the present conjugation in Arabic",
  "future": "future conjugation for 'Yo'",
  "future_meaning": "meaning of the future conjugation in Arabic",
  "sentences": [
    { "sentence": "simple example with infinitive", "translation": "Arabic translation" },
    { "sentence": "simple example with past", "translation": "Arabic translation" },
    { "sentence": "simple example with present", "translation": "Arabic translation" },
    { "sentence": "simple example with future", "translation": "Arabic translation" }
  ]
}

CRITICAL INSTRUCTIONS:
- The output MUST be a valid JSON object.
- Do NOT wrap it in markdown code blocks (\`\`\`json).
- Do NOT include any text outside the JSON object.
- Ensure the sentences are simple and useful for everyday conversation.
  `.trim();

  try {
    const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }]
      })
    });

    if (!response.ok) {
      throw new Error(`Gemini API Error: ${response.status}`);
    }

    const data = await response.json();
    let text = data.candidates?.[0]?.content?.parts?.[0]?.text || "";
    
    // Clean up potential markdown formatting from Gemini response
    text = text.replace(/^```json/i, '').replace(/^```/i, '').replace(/```$/i, '').trim();

    return JSON.parse(text);
  } catch (error) {
    console.error("Error generating verb details:", error);
    throw error;
  }
};
