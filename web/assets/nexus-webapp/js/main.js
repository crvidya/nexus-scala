Nexus.logger.info("Initializing Nexus...");
Nexus.logger.info("Version: " + Nexus.version);
Nexus.logger.info("---------------------");

Nexus.logger.info("Initializing websocket connection");
Nexus.networkManager.setWebsocketUrl(document.location.origin.replace("http://","ws://").replace("https://","wss://") + "/websocket/");
Nexus.networkManager.connectToServer();

//Templates
Nexus.templateLoader.registerPage("application", {title: "Nexus"});
Nexus.templateLoader.registerPage("home");
