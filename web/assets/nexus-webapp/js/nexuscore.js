(function(window, $) {

    function NexusCore(win, jQ) {
        this.logger = new NexusLogger();
        this.networkManager = new NetworkManager(this);
    }
    function NexusLogger() {}
    function NetworkManager(nx) {
        this.nexus = nx;
    }

    var logformat = "dd-mm-yyyy HH:MM:ss";

    NexusLogger.prototype.console = window.console || undefined
    NexusLogger.prototype.debug = function(data){
        if(typeof this.console !== "undefined"){
            this.console.debug(new Date().format(logformat) + " [DEBUG] " + data);
        }
    }
    NexusLogger.prototype.info = function(data){
        if(typeof this.console !== "undefined"){
            this.console.log(new Date().format(logformat) + " [INFO] " + data);
        }
    }
    NexusLogger.prototype.warn = function(data){
        if(typeof this.console !== "undefined"){
            this.console.warn(new Date().format(logformat) + " [WARNING] " + data);
        }
    }
    NexusLogger.prototype.error = function(data){
        if(typeof this.console !== "undefined"){
            this.console.error(new Date().format(logformat) + " [SEVERE] " + data);
        }
    }
    NexusLogger.prototype.onMissingDependency = function(name){
        alert("Dependency " + name + " is missing");
    }

    NetworkManager.prototype.websocketUrl = "";
    NetworkManager.prototype.setWebsocketUrl = function(url){
        this.websocketUrl = url;
    }
    NetworkManager.prototype.connectToServer = function() {
        var self = this;
        this.websocket = new WebSocket(this.websocketUrl);
        this.websocket.onopen = function(){
            self.nexus.logger.info("Connected to the server!");
        }
        this.websocket.onclose = function(){
            self.nexus.logger.info("Server connection closed!");
        }
        this.websocket.onmessage = function(data){
            self.nexus.logger.info("Received: " + data);
        }
        this.websocket.onerror = function(){
            self.nexus.handleFatal("Server connection failed!");
        }
    }

    NexusCore.prototype.version = "3.0";
    NexusCore.prototype.$ = $;
    NexusCore.prototype.BOOTED = false;

    NexusCore.prototype.handleFatal = function(error){
        //TODO: implement
        this.logger.error("Fatal error: " + error);
    }

    window.Nexus = new NexusCore(window, $);

})(window, jQuery);