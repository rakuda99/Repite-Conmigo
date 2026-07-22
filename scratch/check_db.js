const https = require('https');

https.get('https://firestore.googleapis.com/v1/projects/repite-conmigo/databases/(default)/documents/community_decks?pageSize=300', (res) => {
  let data = '';
  res.on('data', (chunk) => {
    data += chunk;
  });
  res.on('end', () => {
    const json = JSON.parse(data);
    const fs = require('fs');
    fs.writeFileSync('scratch/decks_dump.json', JSON.stringify(json, null, 2));
    
    if(json.documents) {
        const sorted = json.documents.sort((a,b) => new Date(b.createTime) - new Date(a.createTime));
        sorted.slice(0, 10).forEach(doc => {
            const title = doc.fields.title?.mapValue?.fields?.['en-US']?.stringValue || doc.fields.title?.mapValue?.fields?.['ar-SA']?.stringValue;
            console.log(`[${doc.createTime}] ${title} (uploadedAt: ${doc.fields.uploadedAt ? 'YES' : 'NO'})`);
        });
    }
    console.log("Dumped to scratch/decks_dump.json");
  });
});
