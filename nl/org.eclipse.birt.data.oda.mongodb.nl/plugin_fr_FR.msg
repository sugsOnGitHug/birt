###############################################################################
# Copyright (c) 2013 Actuate Corporation
# All rights reserved.
#
###############################################################################
# Plug-in Configuration
#
oda.data.source.id=org.eclipse.birt.data.oda.mongodb
#
###############################################################################
# NLS Strings
###############################################################################
#
plugin.name=Pilote d'ex�cution ODA de la source de donn�es MongoDB
data.source.name=Source de donn�es MongoDB
data.set.name=Jeu de donn�es MongoDB
connection.profile.name=Profil de connexion � la source de donn�es MongoDB

# Data source properties
datasource.property.mongoDbUri=URI de la base de donn�es &Mongo
datasource.property.ignoreURI=&Ignorer URI
datasource.property.serverHost=&H�te du serveur
datasource.property.serverPort=P&ort du serveur
datasource.property.databaseName=Nom de la base de &donn�es
datasource.property.userName=Nom d'utili&sateur
datasource.property.password=&Mot de passe
datasource.property.socketKeepAlive=Garder l'interface de connexion &active
datasource.property.useRequestSession=Session de &requ�te consistante
datasource.property.group.uriElements=Uniform Resource Identifier et �l�m&ents de la base de donn�es Mongo
datasource.property.group.options=Param�tre(s) de client &suppl�mentaire

# Data set properties - query definition
dataset.property.collectionName=Nom de la &collection
dataset.property.operationType=Type de commande d'&op�ration
dataset.property.operationExpr=&Expression de commande
dataset.property.selectedFields=Champs s�&lectionn�s
dataset.property.findQueryExpr=Expression de re&qu�te
dataset.property.sortExpr=Expression de &tri
# Data set properties - advanced runtime properties
dataset.property.batchSize=Nombre d'objets de r�sultat maximum par &lot
dataset.property.rtMDSearchLimit=Nombre maximum de documents � chercher pour les &m�tadonn�es d'ex�cution
dataset.property.numSkipDocuments=Nombre maximum de documents � i&gnorer
dataset.property.indexHints=Expression d'astuce d'inde&x
dataset.property.queryReadPreference=Pr�f�rences de lectu&re pour le curseur de requ�te
dataset.property.queryReadPreferenceTags=Jeu(x) de ba&lises de pr�f�rence de lecture
dataset.property.noTimeOut=Aucu&ne expiration de d�lai pour le curseur
dataset.property.allowsPartialResults=Autorise les r�sultats &partiels
dataset.property.flattenCollections=&Aplatir les collections imbriqu�es
#
dataset.property.value.readPrefPrimary=primaire
dataset.property.value.readPrefPrimaryPreferred=primairePr�f�r�
dataset.property.value.readPrefSecondary=secondaire
dataset.property.value.readPrefSecondaryPreferred=secondairePr�f�r�
dataset.property.value.readPrefNearest=le plus proche
