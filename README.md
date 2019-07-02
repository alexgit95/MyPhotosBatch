 MyPhotosBatch

Robot permettant d'inserer les photos dans une base mongo db, les infos recoltées sont :
 
 - la position de la prise de la photo (lattitude, longitude)
 - la date de prise de la photo
 - la ville, la region et le pays de la prise
 
Ces photos sont ensuite regroupées dans des evenements automatiquement genérés par le traitement, prenant en compte leur date, durée et emplacement.
 
## Le traitement se deroule comme suit :

### Début de traitements des nouveaux elements.

 - Si la propriété backup est activé, on sauvegarde le contenu de la collection Photo et la collection evenement dans des fichiers `backup.activation=true`, la sauvegarde sera placée dans le chemin indiqué par la propriété `backup.emplacement.sauvegarde`
 - Si la propriété backup only est à true, on s'arrete la `backup.only=true`
 1. On commence par charger tous les fichiers images placés sous le repertoire photo racine `chargerFichiers(RACINE_ANALYSE);` indiqué dans la propriété `analyse.repertoireRacine`
 2. On filtre les fichiers pour ne prendre que les nouveaux `filterFichierDejaPresent(..)`
 3. On recupere leurs informations complementaire (meta données) `recuperationInfoComplementaire(..)`
 4. On sauvegarde `sauvegardePhotosdansBDD(..)`
 5. On recupere les informations de geocodage de ces photos `fillGeocodageInfo(..)`
 6. On sauvegarde `sauvegardePhotosdansBDD(..)`
 
 ### Fin de traitements des nouveaux elements.
 
 ### Traitement de rattrapage sur toutes les pieces 
 
 Les traitements suivants auront lieu sur toutes les photos, y compris les anciennes
 
 7. On recupere les photos sans geolocalisation
 8. On tente de trouver par rapprochement leur geolocalisation
 9. On tente de geocoder toutes les photos disposant d'une geolocalisation mais sans geocodage
 10. On sauvegarde
 
### Fin des traitements sur toutes les photos
 
### On commence le traitement des evenements 
 
 11. On recupere toutes les photos ayant une date de capture, mais n'ayant pas deja été scanné et n'ayant pas été déjà rattaché à un evenement
 12. On crée des evenements à partir de cette liste triée par date croissante
 
### Fin de traitement des evenements


### Fin d'execution du traitement

## Configuration
 
 Pour recevoir les notifications la clé IFTTT necessite d'etre indiquée  ex : `ifttt.cle=AAAFFFRRRREVVVVEE`
 
 Pour acceder aux fonctions de geocodage, il faut aussi recuperer une clé sur locaationiq et l'indiquer ici : `locationiq.cle=DVBHDVDVGVGFVG`
 
 La configuration de la base se présente comme suit dans le application.properties:
 
 Pour une configuration locale :
 
 ```
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=test
 
 ```
 
 Pour une configuration lié à cloud MongoDB Atlas:
 
 ```
spring.data.mongodb.uri=mongodb+srv://user:pass@opencodez-pzgjy.gcp.mongodb.net/test?retryWrites=true
spring.data.mongodb.database=test
 
 ```
 
 Pour ameliorer les performances de l'application, dans la base il faut creer les index suivants sur la collection photos:
 
 - chemin
 - datePriseVue
 - nom
 
 
 ## Lancer le robot
 
```
  
java -Xmx1024m -jar batchPhoto-0.0.1-SNAPSHOT.jar --spring.config.location=file:chemin/fichier/properties.properties
  
```
 