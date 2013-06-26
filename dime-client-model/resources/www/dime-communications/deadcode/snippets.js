
Dime.psHelper = {

getSAIDFromUrl: function(myUrl){
        var preMainSAIDSector = "rest" //subPath coming directly before the mainSaid - (must be uniq)
        if (!myUrl){
            myUrl = document.URL
        }
        var splittedURL = myUrl.split(/\/+/g);
        
        var i=0;
        while ((i+1)<splittedURL.length){
            if (splittedURL[i]===preMainSAIDSector){
                return splittedURL[i+1];
            }
            i++;
        }
        return ""; //mainSAID not found
        
    },   
    
    
    getFileFromUrl: function(url){
        if (!url){
            return "";
        }
        var splittedURL = url.split(/\/+/g);

        return splittedURL[splittedURL.length-1];
    },
    
    
    
    
}