
(function(){

    function PageLoader(){}

    PageLoader.prototype.pages = []
    PageLoader.prototype.registerPage = function(name){
        var self = this;
        $.get("templates/" + name + ".hbs", function(data){
            self.pages.push({name: name, template: Handlebars.compile(data)});
        });
    }
})()