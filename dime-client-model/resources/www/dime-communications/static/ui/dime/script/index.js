/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

/* 
 *  Description of index.js
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.07.2012
 */


//---------------------------------------------
//#############################################
//  DimeView Manager
//#############################################
//

DimeViewManager = function(){
    //                                               id, groupActive, settingsActive, personViewActive
    this.addToViewMap(new DimeViewManager.viewMapEntry('groupNavigation', false, false, false, this.getCurrentGroupType)); //initially set to false, so it will only be shown with some content in place
    this.addToViewMap(new DimeViewManager.viewMapEntry('itemNavigation', false, false, false, this.getCurrentItemType)); //initially set to false, so it will only be shown with some content in place
    this.addToViewMap(new DimeViewManager.viewMapEntry('searchBox', true, false, false, null));
    this.addToViewMap(new DimeViewManager.viewMapEntry('metabarMetaContainer', true, false, true, null));
    this.addToViewMap(new DimeViewManager.viewMapEntry('backToGroupButton', false, false, true, null));
    this.addToViewMap(new DimeViewManager.viewMapEntry('currentPersonOverview', false, false, true, null));
    this.addToViewMap(new DimeViewManager.viewMapEntry('currentPersonLabel', false, false, true, null));
    this.addToViewMap(new DimeViewManager.viewMapEntry('personProfileAttributeNavigation', false, false, true, Dime.psMap.TYPE.PROFILEATTRIBUTE));
    this.addToViewMap(new DimeViewManager.viewMapEntry('personProfileNavigation', false, false, true, Dime.psMap.TYPE.PROFILE));
    this.addToViewMap(new DimeViewManager.viewMapEntry('personLivepostNavigation', false, false, true, Dime.psMap.TYPE.LIVEPOST));
    this.addToViewMap(new DimeViewManager.viewMapEntry('personDataboxNavigation', false, false, true, Dime.psMap.TYPE.DATABOX));
    this.addToViewMap(new DimeViewManager.viewMapEntry('personResourceNavigation', false, false, true, Dime.psMap.TYPE.RESOURCE));
    this.addToViewMap(new DimeViewManager.viewMapEntry('settingsNavigationContainer', false, true, false, Dime.psMap.TYPE.SERVICEADAPTER));
    //the following are deactivated by default and only shown when required
    this.addToViewMap(new DimeViewManager.viewMapEntry('dropzoneNavigation', false, false, false, null));
    this.addToViewMap(new DimeViewManager.viewMapEntry('globalItemNavigation', false, false, false, null));  
};


DimeViewManager.prototype = {

    //VIEW_MAP
    viewMap:{}, //initialization in the constructor of DimeViewManager
    
    GROUP_CONTAINER_VIEW: 1,
    SETTINGS_VIEW: 2,
    PERSON_VIEW: 3,

    currentView: 0,
    
    groupType:Dime.psMap.TYPE.GROUP,
    itemType:Dime.psMap.TYPE.PERSON,
    currentGuid: null,
    
    getCurrentGroupType: function(){
        return this.groupType;
    },
    
    getCurrentItemType: function(){
        return this.itemType;
    },
    
    
    
    addToViewMap: function(viewMapEntry){
        this.viewMap[viewMapEntry.id]=viewMapEntry;
    },
    
    updateView: function(groupType, viewType, avoidPushingHistory){
        
    },
            
    updateViewExplicit: function(groupType, viewType, guid, detailItemType, detailItemUserId, message){
        this.groupType = groupType;
        this.viewType = viewType;
        this.currentGuid = guid;
        this.detailItemType = detailItemType;
        this.detailItemUserId = detailItemUserId;
        this.message = message;
    },
    
    updateViewFromNotifications: function(notifications){

    },
            
    updateViewForPerson: function(event, element, entry){

    },

    /**
     * constructor for viewMapEntries
     * @param {type} id id of div in index.html
     * @param {type} groupActive
     * @param {type} settingsActive
     * @param {type} personViewActive
     * @param {type} type
     * @returns {undefined}
     */
    viewMapEntry: function(id, groupActive, settingsActive, personViewActive, type){
        this.id = id;
        this.groupActive = groupActive;
        this.settingsActive = settingsActive; 
        this.personViewActive = personViewActive;
        this.type = type;
    }
};
//---------------------------------------------
//#############################################
//  END DimeView Manager
//#############################################
//

//---------------------------------------------
//#############################################
//  DimeView 
//#############################################
//


DimeView = {
    
    searchFilter: "",
    pushState:{}, //browser history to manage back-button
    
    getShortNameWithLength: function(name, length){
        var myName = name;
        if(myName.length>length){
            myName = myName.substr(0, length) + " ..";
        }
        return myName;
    },
    
    getShortName: function(name){
        var myName = name;
        
        
        if (myName.length>24){
            myName = myName.substr(0, 24);
        }
        
        //insert some spaces after 12 letters
        var result = "";
        var hasSpace = false;
        for (var i=0; i<myName.length;i++){
            if (myName[i]===" "){
                hasSpace = true;
            }
            
            result = result + myName[i];
            
            if (i===12 || i===16){
                if (!hasSpace){
                    result = result + " ";     
                    hasSpace=true;
                }else{
                    hasSpace = false;
                }
            }
            
        }
        return result;
    },
    
    actionMenuActivatedForItem: function(entry){
        var result = (entry.userId==='@me'); //TODO improve?
        
        if (entry.editable!==undefined && entry.editable!==null&&entry.editable===false){
            result=false;
        }

        return result;
    },
    

    createMark: function(entry, className, isGroup){
        var result=$('<div/>').addClass('mark').addClass(className);

        if (!DimeView.actionMenuActivatedForItem(entry)){
            result.addClass('noActionMark');
            return result;
        }//else
        
        //new behaviour of selection (old: switching a group led to lost of selection)
        if(!isGroup){
            var allSelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
            for(var j=0; j<allSelectedItems.length; j++){
                if(entry.guid == allSelectedItems[j].guid){
                    result.addClass("ItemChecked");
                }
            }
        }
        
        result.clickExt(DimeView, DimeView.selectItem, entry, isGroup);
        return result;
    },
    
    setActionAttributeForElements: function(entry, jElement, isGroupItem, showEditOnClick){
       
        jElement.mouseoverExt(DimeView, DimeView.showMouseOver, entry, isGroupItem);
        jElement.mouseoutExt(DimeView, DimeView.hideMouseOver, entry);
        
        //FIX: add additional handler for situations
        if(isGroupItem){
            jElement.clickExt(DimeView, DimeView.showGroupMembers, entry);
        }else if (entry.type===Dime.psMap.TYPE.PERSON){
            jElement.clickExt(DimeView.viewManager, DimeView.viewManager.updateViewForPerson, entry);
        }else if(showEditOnClick){
            jElement.clickExt(DimeView, DimeView.editItem, entry);
        }
        
    },
   
    addGroupElement: function(jParent, entry){
   
        var groupClass=(entry.type!==Dime.psMap.TYPE.GROUP?entry.type+"Item groupItem":"groupItem");
        
        var jGroupItem=$('<div/>').addClass(groupClass).append($('<div/>')
                .append(
                    $('<img/>').attr('src', Dime.psHelper.guessLinkURL(entry.imageUrl)))
                .append(
                    DimeView.createMark(entry, "", true)
                )
                .append('<div class="groupItemCounter" ><h1>'+ entry.items.length + '</h1></div>')
                .append('<div class="clear"></div>')
        
                //additional hint: "click to edit"
                .append(
                    $('<div/>')
                        .addClass('captionForGroupElement')
                        .attr('title', entry.name)
                        .append(
                            $('<h4>'+ DimeView.getShortNameWithLength(entry.name, 11) + '</h4>')
                        )
                        .append(
                            $('<div class="editHintGroupElement"></div>')  
                        )
                        .hover(function(){
                                $(this).children('.editHintGroupElement').append('(click to edit)');
                            }, function(){
                                $(this).children('.editHintGroupElement').empty();
                        })
                        .clickExt(DimeView, DimeView.editItem, entry)
                )
        );
   

        DimeView.setActionAttributeForElements(entry, jGroupItem, true, false);

        jParent.append(jGroupItem);

    },    
    
    createAttributeItemJElement: function(entry){
        var jChildItem = $('<div/>').addClass("childItemProfileAttribute")
            .append(DimeView.createMark(entry, "profileAttributeMark", false))
            .append('<div class="profileAttributeCategory">'
                    +Dime.PACategory.getCategoryByName(entry.category).caption+'</div>')
            .append('<div class="profileAttributeName">'+ entry.name.substr(0, 23) + '</div>');

        var profileAttributeValues = $('<div class="profileAttributeValues"/>');

        if (entry.value){
            for (var key in entry.value){
                var value = entry.value[key];
                if (value && value.length>0){
                    profileAttributeValues.append(
                            $('<div class="profileAttributeValue"/>')
                            .append('<span class="profileAttributeValueKey"/>').text(key)
                            .append('<span class="profileAttributeValueValue"/>').text(value)
                            );
                }
            }
        }
        jChildItem.append(profileAttributeValues)
            .append('<div class="clear">');
        
        
        DimeView.setActionAttributeForElements(entry, jChildItem, false, true);
        
        return jChildItem;
    },

    createUserNotification: function(entry){

        var jChildItem = $("<div/>");

        var markRead=function(){
            entry.read=true;
            Dime.REST.updateItem(entry);
            jChildItem.addClass('userNotificationWasRead');
        };


        //classes
        var itemClass=entry.type+"Item childItem";
        jChildItem.addClass(itemClass);
        if (entry.read){
            jChildItem.addClass('userNotificationWasRead');
        }else{
            jChildItem.click(markRead);
        }
        
        var unValues=Dime.un.getCaptionImageUrl(entry);
        
        if (entry.unType===Dime.psMap.UN_TYPE.REF_TO_ITEM){
            var elementType=entry.unEntry.type;

            var groupType = elementType;
            if (Dime.psHelper.isChildType(elementType)){
                groupType=Dime.psHelper.getParentType(elementType);
            }

            var guid = encodeURIComponent(entry.unEntry.guid);
            var userId=entry.unEntry.userId;
            if (userId!=='@me'){
                userId=encodeURIComponent(userId);
            }

            var target = "self.location.href='index.html?type="+ groupType
                +"&guid="+guid
                +"&userId="+userId
                +"&dItemType="+elementType
                +"&msg="+unValues.caption
                +"'";

            jChildItem.attr("onclick", target);
        }else{
            jChildItem.click(function(){
                //TODO fix
                window.alert("This function is not supported in the research prototype.");
            });
        }

        //img
        jChildItem.append($('<img/>').attr('src',Dime.psHelper.guessLinkURL(entry.imageUrl)));
        
        jChildItem
            .append($('<img/>').attr('src', unValues.imageUrl).addClass('childItemNotifElemType'))
            .append('<h4 style="font-size: 12px">'+ unValues.caption + '</h4>')
            .append($('<div/>').addClass('childItemNotifOperation').append('<span>'+ unValues.operationName + '</span>'))
            .append($('<span/>').addClass("childItemNotifElemCaption").text(unValues.childName)
            );

        return jChildItem;
    },
            
    createLocationItemJElement: function(entry){
        
        var idSocial = "#" + entry.guid + "Social";
        var idOwn = "#" + entry.guid + "Own";
        var fav = entry.favorite?"favorite":false;
        var jChildItem = $("<div/>");
        var itemClass = entry.type + "Item childItem";
        
        jChildItem.attr("id", entry.guid + "Div");
        jChildItem.addClass(itemClass);
        
        //get current placeGuid stored in #currentPlaceGuid
        var currentPlaceGuid = document.getElementById("currentPlaceGuid").getAttribute("data-guid");
        if(entry.guid == currentPlaceGuid){
            jChildItem.addClass("highlightCurrentPlaceItem");
        }else{
            jChildItem.removeClass("highlightCurrentPlaceItem");
        }
        
        //replace resource.png (no-image) with default
        jChildItem.append('<img src="'+ Dime.psHelper.guessLinkURL(entry.imageUrl.replace("resource.png","place_default.png"))+ '" />');
        jChildItem.append(DimeView.createMark(entry, "", false));
        jChildItem.append('<h4 title="' + entry.name + '"><b>'+ DimeView.getShortNameWithLength(entry.name, 40) +  '</b></h4>');
        if(fav){
            jChildItem.append('<p>' + fav + '</p>');
        }
        
        jChildItem.append(
                $("<div></div>")
                    .addClass("ratingContainerSocial")
                    .append(
                        $("<div></div>")
                            .addClass("ratingStarsSocial")
                            .text("Social Rating: ")
                            .raty({
                                width: 175,
                                half: true,
                                readOnly: true,
                                score: entry.socialRecRating*5
                            })
                    )
        );
        
        jChildItem.append(
                $("<div></div>")
                    .addClass("ratingContainerOwn")
                    .append(
                        $("<div></div>")
                            .addClass("ratingStarsOwn")
                            .text("Your Rating: ")
                            .raty({
                                width: 175,
                                half: true,
                                readOnly: true,
                                score: entry.userRating*5
                            })
                    )
        );
        
        jChildItem.append('<div class="ratingContainerOwn"><div class="rateStarsOwn" id="' + entry.guid + 'Own"></div></div>');
        DimeView.setActionAttributeForElements(entry, jChildItem, false, entry.type);
        return jChildItem;
    },
    
    createItemJElement: function(entry){
        //handle profileattributes separately
        if (entry.type===Dime.psMap.TYPE.PROFILEATTRIBUTE){
            return DimeView.createAttributeItemJElement(entry);
        }else if (entry.type===Dime.psMap.TYPE.USERNOTIFICATION) {
            return DimeView.createUserNotification(entry);
        }else if (entry.type===Dime.psMap.TYPE.PLACE){
            return DimeView.createLocationItemJElement(entry);
        }
        
        var showEditOnClick = (entry.type===Dime.psMap.TYPE.SITUATION)
                    || (entry.type===Dime.psMap.TYPE.PLACE)
                    || (entry.type===Dime.psMap.TYPE.RESOURCE)
                    || (entry.type===Dime.psMap.TYPE.LIVEPOST);
        
                
        var jChildItem = $("<div/>");
        
        //classes
        var itemClass=entry.type+"Item childItem";
        
        if (entry.type===Dime.psMap.TYPE.SITUATION){
            if (entry.active){
                itemClass += " childItemSituationActive";
            }
        }        
        jChildItem.addClass(itemClass);
        
        
        //innerChild
        //innerChild - img        
        if (entry.type===Dime.psMap.TYPE.PERSON){
            jChildItem.append('<div class="wrapProfileImage" ><img src="'+ Dime.psHelper.guessLinkURL(entry.imageUrl)+ '" /></div>');
        }else{
            jChildItem.append('<img src="'+ Dime.psHelper.guessLinkURL(entry.imageUrl)+ '" />');
        }
        
        //innerChild - mark
        jChildItem.append(DimeView.createMark(entry, "", false));
        
        //innerChild - name
        if (entry.type===Dime.psMap.TYPE.LIVEPOST){
            var entryName = DimeView.getShortNameWithLength(entry.name, 125);
        }else{
            var entryName = DimeView.getShortNameWithLength(entry.name, 30);
        }
        
        if (entry.type!==Dime.psMap.TYPE.USERNOTIFICATION){
            jChildItem.append('<h4>'+ entryName + '</h4>');
        }
          
        //innerChild - type specific fields
        if (entry.type===Dime.psMap.TYPE.LIVEPOST && entry.text){
            jChildItem.append(
                $('<div>').addClass('childItemLivepostText').text(entry.text.substr(0, 150))
            );
        }                
        //set action attributes
        DimeView.setActionAttributeForElements(entry, jChildItem, false, showEditOnClick);
        return jChildItem;
    },
    
    
    addItemElement: function(jParent, entry){
        jParent.append(DimeView.createItemJElement(entry));

        
    },
    
    CONTAINER_ID_INFORMATION: 1,
    CONTAINER_ID_SHAREDWITH: 2,
    
    
    /**
     * returns a container by id
     * @param {String} containerId valid ids are  CONTAINER_ID_INFORMATION CONTAINER_ID_SHAREDWITH
     */
    getMetaListContainer: function(containerId){
        if (containerId === DimeView.CONTAINER_ID_INFORMATION){
            return $("#metaDataInformationContainer");
        }else if(containerId === DimeView.CONTAINER_ID_SHAREDWITH){
            return $("#metaDataSharedWithContainer");
        }//else
        throw "containerId not supported: "+containerId;        
    },
    
    clearMetaBar: function(){
        DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION).empty();
        DimeView.getMetaListContainer(DimeView.CONTAINER_ID_SHAREDWITH).empty();
        
    },
    
    groupEntries: [],
    
    itemEntries: [],

    initContainer: function(jContainer, caption, selectingGroupName){  

        var containerCaption = JSTool.upCaseFirstLetter(caption);
        if (selectingGroupName){
            containerCaption += ' in "' +selectingGroupName+'"';
        }else if (DimeView.searchFilter.length===0){
            if(jContainer.selector !== "#groupNavigation"){
                containerCaption = "All "+containerCaption;
            }
        }

        jContainer.empty();
        jContainer.append(
            $('<div/>').attr('id','containerCaption').addClass('h2ModalScreen')
                .text(containerCaption)
                .append($('<div/>').addClass('clear')));

    },
    
    handleSearchResultForContainer: function(type, entries, jContainerElement, isGroupContainer){

        if (!entries || entries.length===0){
            jContainerElement.addClass('hidden');
            return;
        }

        var isSubString=function(fullString, subString){
            return (subString.toLowerCase().indexOf(fullString.toLowerCase())!==-1);
        };

        var isInFilter = function(entry){
            return isSubString(DimeView.searchFilter, entry.name);
        };

        //special search when searching on usernotifications
        if (type===Dime.psMap.TYPE.USERNOTIFICATION){
            isInFilter = function(entry){

                var unValues=Dime.un.getCaptionImageUrl(entry);

                var result =
                    isSubString(DimeView.searchFilter, entry.name)
                    || isSubString(DimeView.searchFilter, unValues.caption)
                    || isSubString(DimeView.searchFilter, unValues.operation)
                    || isSubString(DimeView.searchFilter, unValues.childName)
                    || isSubString(DimeView.searchFilter, unValues.shortCaption)
                ;

                return result;
            };
        }

        //for @me profiles we skip profiles with no said
        if (type===Dime.psMap.TYPE.PROFILE){
            isInFilter = function(entry){
                if ((entry.userId==='@me')
                    
                    &&(!entry.said || entry.said.length<1)){
                    return false; //skip this
                }
                return isSubString(DimeView.searchFilter, entry.name);
                //TODO - also search in attributes
            };

        }


        DimeView.initContainer(jContainerElement, Dime.psHelper.getPluralCaptionForItemType(entries[0].type));

        jContainerElement.removeClass('hidden');

        for (var i=0; i<entries.length; i++){ 
            if (isInFilter(entries[i])){
                if (isGroupContainer){
                    DimeView.addGroupElement(jContainerElement, entries[i]);

                }else{                    
                    DimeView.addItemElement(jContainerElement, entries[i]);
                }
            }
        }
    }, 
    
    handleSearchResult: function(entries, type){          
        
        //select container element
        var isGroupContainer=Dime.psHelper.isParentType(type);
        
        var jContainerElement;
        if (isGroupContainer){
            jContainerElement=$('#groupNavigation');
        }else{
            jContainerElement=$('#itemNavigation');
        }
        DimeView.handleSearchResultForContainer(type, entries, jContainerElement, isGroupContainer);
    }, 
    
    selectedItems: {},
    
    clearSelectedItems: function(){
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        for (var i=0;i<mySelectedItems.length;i++){
            var item = mySelectedItems[i];
            if (item){
                item.element.removeClass("ItemChecked");
            }
        }
        DimeView.selectedItems = {};
        $("#globalActionButton").empty();
        DimeView.updateActionView(0);
    },
    
    ACTION_BUTTON_ID:[
        {
            id: "actionButtonNew",
            minItems: -1,
            maxItems: -1
        },
        {
            id: "actionButtonEdit",
            minItems: 1,
            maxItems: 1
        },
        {
            id: "actionButtonAddRemove",
            minItems: -1,
            maxItems: 0
        },
        {
            id: "actionButtonDelete",
            minItems: 1,
            maxItems: -1
        },
        {
            id: "actionButtonShare",
            minItems: -1,
            maxItems: -1
        },
        {
            id: "actionButtonMore",
            minItems: -1,
            maxItems: -1
        }
    ],
    
    updateActionView: function(selectionCount){
        //var disabledButtonClass = "actionButtonDisabled";
        var disabledButtonClass = "disabled";
        
        var hideButton=function(buttonId){
            var myButton = $("#"+buttonId);
            if (!myButton.hasClass()){
                myButton.addClass(disabledButtonClass);
            }
        };
    
        var showButton=function(buttonId){
            var myButton = $("#"+buttonId);
            myButton.removeClass(disabledButtonClass);
        };
        
        for (var i=0;i<DimeView.ACTION_BUTTON_ID.length;i++){
            
            var actionButton=DimeView.ACTION_BUTTON_ID[i];
            
            if (actionButton.minItems===-1){ //always show this
                //since this is never removed no explicit show required
                continue;
            }
            if (selectionCount<actionButton.minItems){ 
                hideButton(actionButton.id);
                                   
            } else if ((actionButton.maxItems===-1) || (selectionCount<=actionButton.maxItems)){ 
                showButton(actionButton.id);
                
            }else{ //too many selected
                hideButton(actionButton.id);
            }
        }
    },
    
    selectItem: function(event, element, entry, isGroupItem){
        
        if (event){
            event.stopPropagation();
        }
        var guid=entry.guid;
        
        //new behaviour of selection
        var selectedItemsAll = JSTool.getDefinedMembers(DimeView.selectedItems);
        var lastMemberCount = selectedItemsAll.length;
        if(DimeView.selectedItems[guid]){
            DimeView.selectedItems[guid] = null;
            $(element).removeClass("ItemChecked");
        }else if(lastMemberCount == 0 || entry.type == selectedItemsAll[lastMemberCount-1].type){
            DimeView.selectedItems[guid] = { //FIXME refactor - only use a hash with guid as key for all items!!!
                guid:guid,
                userId:entry.userId,
                type:entry.type,
                isGroupItem:isGroupItem,
                element:element
            };
            $(element).addClass("ItemChecked");
        }else{
            DimeView.selectedItems = {};
            $(".groupChecked").removeClass("ItemChecked");
            $(".personItem").removeClass("ItemChecked");
            DimeView.selectedItems[guid] = { //FIXME refactor - only use a hash with guid as key for all items!!!
                guid:guid,
                userId:entry.userId,
                type:entry.type,
                isGroupItem:isGroupItem,
                element:element
            };
            $(element).addClass("ItemChecked");
        }
        
        var memberCount = JSTool.countDefinedMembers(DimeView.selectedItems);     
        $("#globalActionButton").text(memberCount);
        
        this.updateActionView(memberCount);
        
        if(isGroupItem){
            //when selected, set focus on groupElement
            this.showGroupMembers(event, element, entry);
            element.parent().parent().addClass("groupChecked");
        }

    },
    
    showSelectedItems: function(){
        //FIXME show this nicely
        var message = "Selected items:\n";
        
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        for (var i=0;i<mySelectedItems.length;i++){
            var item = mySelectedItems[i];
            message += item.type + ": "+item.guid+"("+item.userId+")\n";
        }
        
        window.alert(message);
    },
    
    createMetaBarListItem: function(name, value, imageUrl, className){

        if (value===undefined || value===null){
            //show undefined and null values in the browsers
            //while empty string is allowed
            value="undefined";
        }else if (value.length>22){
            value=value.substr(0, 20)+" ..";
        }

        var listIconElement=$('<div/>').addClass('listElementIcon');
        if (imageUrl){
            listIconElement.append('<img src="' + imageUrl+ '"/>');
        }
        var listItem=$('<li/>').addClass('listElement');
        if (className){
            listItem.addClass(className);
        }

        listItem.append(
                $('<div class="listElementText"/>')
                    .append($('<span class="listElementTextName"/>').text(DimeView.getShortNameWithLength(name, 50)))
                    .append($('<span class="listElementTextValue"/>').text(value))
                )
                .append(listIconElement);
        
        return listItem;
    },

    createMetaBarListItemForItem: function(item){
        var result = DimeView.createMetaBarListItem(
            Dime.psHelper.getCaptionForItemType(item.type),
            item.name,
            item.imageUrl
        );
        //TODO add link to item

        return result;
    },
    
    
  
    addInformationToMetabar: function(entry){
        $("#metaDataInformationHeader").text("Information");

        var informationViewContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION);  
        
        //informationViewContainer.append(DimeView.createMetaBarListItem(entry.name,"", entry.imageUrl));
        informationViewContainer.append(DimeView.createMetaBarListItem(DimeView.getShortNameWithLength(entry.name, 35), "", entry.imageUrl));
        informationViewContainer.append(DimeView.createMetaBarListItem(
            "changed:", JSTool.millisToFormatString(entry.lastModified), null));
             
        
        if (entry.userId!=='@me'){
            var setProviderName=function(response){
                if (response){
                informationViewContainer.append(DimeView.createMetaBarListItem(
                    "shared by:", response.name, response.imageUrl));
                }
            };
            Dime.REST.getItem(entry.userId, Dime.psMap.TYPE.PERSON, setProviderName, '@me', this);
            
        }
        if (entry.hasOwnProperty("nao:trustLevel")){
            
            var tCaptionAndClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(entry["nao:trustLevel"], false);
            if (!tCaptionAndClass){
                return;
            }                
           
            var tElement = DimeView.createMetaBarListItem("trust:", tCaptionAndClass.caption, null);
            tElement.addClass(tCaptionAndClass.classString);
            informationViewContainer.append(tElement);
        }
        if (entry.hasOwnProperty("nao:privacyLevel")){
            var pCaptionAndClass=Dime.privacyTrust.getClassAndCaptionForPrivacyTrust(entry["nao:privacyLevel"], true);
            if (!pCaptionAndClass){
                return;
            }                
           
            var pElement = DimeView.createMetaBarListItem("privacy:", pCaptionAndClass.caption, null);
            pElement.addClass(pCaptionAndClass.classString);
            informationViewContainer.append(pElement);
        }
    },
        
    updateMetabarForSelection: function(){
        DimeView.previousHoverGUID = 0;
        
        DimeView.clearMetaBar(); 
        
        //show selected items summary
        //FIXME - more efficient solution should generate this on select and then only load the items
        
       
        var informationViewContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_INFORMATION);        
        
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        for (var i=0;i<mySelectedItems.length;i++){
            var updateInformationViewContainer = function(response){
                informationViewContainer.append(DimeView.createMetaBarListItemForItem(response));
            };
            Dime.REST.getItem(mySelectedItems[i].guid, mySelectedItems[i].type, updateInformationViewContainer,
                mySelectedItems[i].userId, this);
        }
    
        $("#metaDataInformationHeader").text("Selected");        
        $("#metaDataShareAreaId").addClass("hidden");
      
    },
    
    previousHoverGUID:null,

    hideMouseOver: function(event, element, entry){
        
        if ((!DimeView.previousHoverGUID) || DimeView.previousHoverGUID===0){ //avoid multiple calls
            return;
        }
        
        if (!event) {event = window.event;}
	var tg = (window.event) ? event.srcElement : event.target;
	if (tg.nodeName !== 'DIV') {
            return;
        }
	var reltg = (event.relatedTarget) ? event.relatedTarget : event.toElement;
	while (reltg !== tg && reltg && reltg.nodeName !== 'BODY'){
		reltg= reltg.parentNode;
        }
	if (reltg===tg) {
            return;
        }
        
	// Mouseout took place when mouse actually left layer
	// Handle event
        //commented out in order to stay visible after mouseout (web ui issue)
        //won't show list of selected items on the metabar
        //DimeView.updateMetabarForSelection();
       
    },
       

    showMouseOver: function(event, element, entry, isGroupEntry){        
        
        //FIXME - this check is not required if this is already checked at a prior instance
        if (!entry  || !entry.guid || !entry.userId || !entry.type || entry.type.length===0 ){
            console.log("ERROR: received invalid entry: (entry)", entry);
            return;
        }
        
        //ATTENTION - this solution is not realy thread-safe - see also comment below...
        if (entry.guid===DimeView.previousHoverGUID){ //avoid reloading the same user again
            return;
        }
        var guid = entry.guid;
        
        DimeView.previousHoverGUID = guid;
        
        DimeView.clearMetaBar(); //TODO -  ATTENTION !!!
        //this solution cannot guarantee that after calling clearMetaBar 
        //- still old callbacks are incoming and added to the meta bar of a 
        //different person that has been hovered on in the meantime...
        //better would be some synchronous call or -better- handling, so
        //it's assured all data belongs to the same person
        //this could be done by using the same approach as for initalProcessor
        
        
        
        JSTool.removeClassIfSet($("#metaDataShareAreaId"),"hidden");
        DimeView.addInformationToMetabar(entry);      
        
        
        
        var addAgentsToMetaBar=function(listItemContainer, agentItems){
            for (var j=0;j<agentItems.length;j++){
                listItemContainer.append(DimeView.createMetaBarListItem( "", agentItems[j].name, agentItems[j].imageUrl, "metaDataShareItem"));
            }
        };
        
        var handleSharedToAgentResult=function(acl){
        
            //rewrite the GUID in case the callback is handled at a later time
            //so it will be overwritten again on the next mouse-over call
            DimeView.previousHoverGUID = guid; //TODO check potential consequences for rewriting
            
            
            if (acl.length===0){
                return;
            }

            var listItemContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_SHAREDWITH);
        
            for (var i=0;i<acl.length;i++){ //TODO adapt for more information (e.g. show said) in meta-bar
                var aclPackage = acl[i];
                if (!aclPackage.saidSender){
                    console.log("ERROR: oops - received aclPackage without saidSender - skipping!", aclPackage);
                    continue;
                }
                var writeACLPackage = function(profile){
                    var pName = (profile?profile.name:"No profile for "+aclPackage.saidSender);
                    var pImage = (profile?profile.imageUrl:null);
                    var profileContainerList = $('<ul/>');
                    var profileContainer = DimeView.createMetaBarListItem("shared as:", pName, pImage, "metaDataShareProfile")
                        .append(profileContainerList);
                    listItemContainer.append(profileContainer);
                    addAgentsToMetaBar(profileContainerList, aclPackage.groupItems);
                    addAgentsToMetaBar(profileContainerList, aclPackage.personItems);
                    addAgentsToMetaBar(profileContainerList, aclPackage.serviceItems);
                };

                Dime.psHelper.getProfileForSaid(aclPackage.saidSender, writeACLPackage);

                
            }
        };
        
        var handleSharedItemsResult=function(resultItems){
            //rewrite the GUID in case the callback is handled at a later time
            //so it will be overwritten again on the next mouse-over call
            DimeView.previousHoverGUID = guid; //TODO check potential consequences for rewriting
            
            
            if (resultItems.length===0){
                return;
            }
            var listItemContainer = DimeView.getMetaListContainer(DimeView.CONTAINER_ID_SHAREDWITH);
        
            for (var i=0;i<resultItems.length;i++){                        
                var entry = resultItems[i];
                if (!entry.name){
                    console.log("ERROR: oops - received entry without name - skipping!", entry);
                    continue;
                }
            
                listItemContainer.append(DimeView.createMetaBarListItem(entry.name, "", entry.imageUrl));
            }
        };
        
        var isAgent = Dime.psHelper.isAgentType(entry.type);
        if (isAgent){
            Dime.psHelper.getAllSharedItems(entry, handleSharedItemsResult);
        }else if (Dime.psHelper.isShareableType(entry.type) ){
            Dime.psHelper.getAllReceiversOfItem(entry, handleSharedToAgentResult);
        }
    },
        
    updateItemContainerFromArray: function(entries, selectingGroupName){

        DimeView.initContainer($('#itemNavigation'), 
            Dime.psHelper.getPluralCaptionForItemType(DimeView.itemType),
            selectingGroupName);

        var itemContainer = $('#itemNavigation');
        for (var i=0; i<entries.length; i++){ 
            if (entries[i].name.toLowerCase().indexOf(DimeView.searchFilter.toLowerCase())!==-1){              
                DimeView.addItemElement(itemContainer, entries[i]);
            }
        }  
    },
    
    showGroupMembers: function(event, element, groupEntry){
        event.stopPropagation();
        
        if ((!groupEntry) || (!groupEntry.items)){
         return;
        }
        
        $('.groupItem').removeClass('groupChecked');
        element.addClass('groupChecked');

        var updateGroupMembers = function(response){
            this.updateItemContainerFromArray(response, groupEntry.name);
        };

        Dime.REST.getItems(groupEntry.items,Dime.psHelper.getChildType(groupEntry.type), updateGroupMembers, groupEntry.userId, this);
                           
    }, 
    
    editItem: function(event, element, entry, message){
        if (event){
            event.stopPropagation();
        }

        Dime.evaluation.createAndSendEvaluationItemForAction("action_editItem");
        var isEditable=DimeView.actionMenuActivatedForItem(entry);
        
        if (entry.type===Dime.psMap.TYPE.LIVEPOST){
            isEditable=false;
        }

        Dime.Dialog.showDetailItemModal(entry, isEditable, message);
    },
    
    editSelected: function(){

        Dime.evaluation.createAndSendEvaluationItemForAction("action_editItem");

        var selectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        if (selectedItems.length!==1){
            window.alert("Please select a single item.");
            return;
        }
        var triggerDialog=function(response){
            var isEditable=DimeView.actionMenuActivatedForItem(response);
            Dime.Dialog.showDetailItemModal(response, isEditable);
        };

        Dime.REST.getItem(selectedItems[0].guid, selectedItems[0].type, triggerDialog, selectedItems[0].userId, this);
    },
    
    removeSelected: function(){
        var mySelectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        
        if (mySelectedItems.length<1){
            window.alert("Please select at least one item to be deleted!");
            return;
        }

        if (confirm("Are you sure, you want to delete "+mySelectedItems.length+" items?")){
            
            Dime.evaluation.createAndSendEvaluationItemForAction("action_removePerson");

            for (var i=0;i<mySelectedItems.length;i++){
                var item = mySelectedItems[i];
                Dime.REST.removeItem(item);
            }
        }
    },    
           
    
    shareSelected: function(){
        Dime.evaluation.createAndSendEvaluationItemForAction("action_share");

        var selectedItems = JSTool.getDefinedMembers(DimeView.selectedItems);
        
        var triggerDialog=function(response){

            Dime.Dialog.showShareWithSelection(response);
        };

        Dime.psHelper.getMixedItems(selectedItems, triggerDialog, this);
    },
        
    

    /*
     * @param: entry {"surname":"Thiel",
     *  "name":"Simon",
     *  "nickname":"SIT",
     *  "said":"account:urn:4e46c7ec-8e19-45eb-953c-91f68583fd39"},
     */
    createGlobalSearchResultElement: function(entry){
        
        var createNameEntryString = function(baseRef, key, idPostFix){
            if (!baseRef[key] || baseRef[key].lenght===0){
                return "";
            }            
            return $('<span class="globalSearchResult'+idPostFix+'">'
                    +baseRef[key]+'</span>');
        };
                    
        var myJElement = $('<div/>').addClass("publicContactEntry")
            .append($('<div/>').addClass('addPublicPersonBtn btn')
                    .clickExt(DimeView, DimeView.addPublicPerson, entry.said)
                    .text('add'))
            .append('<div class="globalSearchResultBackgroundText">Public Profile</div>')
            .append(
                $('<div class="globalSearchResultName"/>')
                    .append(createNameEntryString(entry, "name","FirstName"))
                    .append(createNameEntryString(entry, "surname","SurName"))
                    .append(createNameEntryString(entry, "nickname","NickName"))
                    );

        return myJElement;
    
    },
        
    addPublicPerson: function(event, jqueryItem, said){
        if (!said || said.length===0){
            console.log("ERROR: said is not said!");
            return;
        }
    
        for (var i=0; i<DimeView.globalSearchResults.length; i++){
            var myResult = DimeView.globalSearchResults[i];
            if (myResult && myResult.said===said){
                
                Dime.REST.addPublicContact(myResult);    
                DimeView.resetSearch();
                return;
            }
        }
        console.log("ERROR: said not found in DimeView.globalSearchResults!");
    },    
    
    globalSearchResults:[],
    
    handleGlobalSearchResult: function(response){
        
        var jGlobalSearchResultContainer = $('#globalItemNavigation');
        
        if (!response || response.length===0){
            jGlobalSearchResultContainer.addClass("hidden");
            return;
        }else{
            jGlobalSearchResultContainer.removeClass("hidden");
        }
        
        DimeView.globalSearchResults = response;
      
        
        
        for (var i=0; i<response.length;i++){
            var jElement = DimeView.createGlobalSearchResultElement(response[i]);
            jGlobalSearchResultContainer.append(jElement);
        }
    },
    
    searchCallForType: function(type){
      
        var callBack=function(response){
            DimeView.handleSearchResult(response, type);
        };
        //HACK??
        if (type===Dime.psMap.TYPE.LIVEPOST){
            Dime.REST.getAllAll(type, callBack);
            return;
        }

        Dime.REST.getAll(type, callBack);
    },
    
    cleanUpView: function(){
        //clear container
        $('#groupNavigation').empty();
        $('#itemNavigation').empty();

        DimeView.clearSelectedItems();
        DimeView.clearMetaBar(); 

        DimeView.globalSearchResults =[];        
  
    },
    
    fileUploader: null,
    
    initFileUploaderForDropzone: function(){
        if (DimeView.fileUploader){
            return;
        }
        
        DimeView.fileUploader = new qq.FileUploader({
            element: document.getElementById("dropzoneNavigationBtn"),
            action: Dime.psHelper.getPathForImageUpload(),
            uploadButtonText: 'upload resource',
            debug: true,            
            multiple: false,        
            forceMultipart: false,
            hideDropzones: false,
            onComplete: function(id, fileName, responseJSON){
                console.log("received response:", responseJSON);
                if (responseJSON 
                    && responseJSON.response
                    && responseJSON.response.meta
                    && responseJSON.response.meta.status               
                    && (responseJSON.response.meta.status.toLowerCase()==="ok")
                    ){
                            
                    responseJSON.success=true; //set success == true for processing of result by fileuploader
                    DimeView.search(); //update container
                }
      
            }
            
        }); 
    },
    
    resetSearch: function(){
        
        var searchText = document.getElementById('searchText');
        searchText.value="";
        DimeView.search();
    },
    
    search: function(){

        var searchText = document.getElementById('searchText');
        console.log('search:', searchText.value);    
        DimeView.searchFilter = searchText.value;

        DimeView.cleanUpView();

        if (DimeView.viewManager.getCurrentGroupType()===Dime.psMap.TYPE.PLACE ){
            var continueSearch = true;
            Dime.psHelper.canRetrievePlaces(function(connected){
                if(!connected){
                    //(new Dime.Dialog.Toast('To activate support for places, please connect to the YellowMapPlaceService in the settings tab!')).show(10*1000);
                    //window.alert('To activate support for places, please connect to the YellowMapPlaceService in the settings tab!');
                    //(new Dime.Dialog.Alert('To activate support for places, please connect to the YellowMapPlaceService in the settings tab!')).show();
                    $("#alertStatusNavigation")
                            .empty() 
                            .removeClass("hidden")
                            .append('To see places nearby to you, please')
                            .append('<br>1. Go to Settings and add the "YellowmapPlaceService"')
                            .append('<br>2. Click “Get location” (in the bar on the right), follow the instructions in the browser');
                    continueSearch=false;
                }
            },this);
            if (!continueSearch){
                return;
            }
        }
        
        if (DimeView.viewManager.getCurrentGroupType()
            && (DimeView.viewManager.getCurrentGroupType()!==Dime.psMap.TYPE.LIVESTREAM) //HACK avoid call for unsupported livestream
        ){
            DimeView.searchCallForType(DimeView.viewManager.getCurrentGroupType());
            
            //also search on global search if groupType==GROUP
            if (DimeView.viewManager.getCurrentGroupType()===Dime.psMap.TYPE.GROUP && searchText.value && (searchText.value.length>0)){

                DimeView.initContainer($('#globalItemNavigation'), "di.me Users in the di.me User Directory");
                
                Dime.REST.searchGlobal(searchText.value, DimeView.handleGlobalSearchResult);
                
            }else{
                $('#globalItemNavigation').addClass("hidden");
            }
        }else if(DimeView.viewManager.getCurrentGroupType()===Dime.psMap.TYPE.LIVESTREAM){ //HACK avoid call for unsupported livestream
            $("#groupNavigation").addClass("hidden");
        }


        if (DimeView.viewManager.getCurrentItemType()){ 
            DimeView.searchCallForType(DimeView.viewManager.getCurrentItemType());
        }
        
       
    },

    updateNewButton: function(groupType){
        $('#actionButtonNew').removeClass('disabled');

        var createPAMenuItems=function(){
            var result=[];
            var type = Dime.psMap.TYPE.PROFILEATTRIBUTE;
            var paCategories = Dime.PACategory.getListOfCategories();

            jQuery.each(paCategories, function(){

                var pACategory = this;
                var link= $('<a tabindex="-1" href="#" />')
                .append('<span style="margin-left: 5px;">- '+pACategory.caption+' ..</span>')
                .clickExt(Dime.Dialog,function(){
                        var newItem = Dime.psHelper.createNewItem(type, "My "+pACategory.caption);
                        newItem.category=pACategory.name;
                        Dime.Dialog.showNewItemModal(type, null, newItem);
                    });

                result.push($('<li/>').attr('role','menuitem').append(link));
            });

            return result;
        };

        //populate new dialog
        var createMenuItem = function(type){
            var link= $('<a tabindex="-1" href="#" />')
                .text('New '+Dime.psHelper.getCaptionForItemType(type)+' ..')
                .clickExt(Dime.Dialog,
                    function(event, jElement, type){Dime.Dialog.showNewItemModal(type);}, type);
            return $('<li/>').attr('role','menuitem').append(link);
        };
        var dropDownUl=$('#actionButtonDropDownNew');
        dropDownUl.empty();

        if (groupType===Dime.psMap.TYPE.GROUP){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.GROUP))
                .append(createMenuItem(Dime.psMap.TYPE.PERSON));

        } else if(groupType===Dime.psMap.TYPE.DATABOX){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.DATABOX))
                .append(createMenuItem(Dime.psMap.TYPE.RESOURCE));

        }else if(groupType===Dime.psMap.TYPE.LIVESTREAM){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.LIVEPOST));

        } else if(groupType===Dime.psMap.TYPE.PROFILE){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.PROFILE))
                .append($('<li/>').attr('role','menuitem')
                    .addClass('newMenuItemGroupCaption')
                        .text('New '+Dime.psHelper.getCaptionForItemType(Dime.psMap.TYPE.PROFILEATTRIBUTE))
                    );
            
            var paItems = createPAMenuItems();
            for (var j=0;j<paItems.length;j++){
                dropDownUl.append(paItems[j]);
            }   

        } else if(groupType===Dime.psMap.TYPE.SITUATION){
            dropDownUl
                .append(createMenuItem(Dime.psMap.TYPE.SITUATION));

        } else if(groupType===Dime.psMap.TYPE.PLACE){
            $('#actionButtonNew').addClass('disabled');
        }

    },

    updateShareButton: function(groupType){
        var shareBtn = $('#actionButtonShare');

         if(groupType===Dime.psMap.TYPE.SITUATION){
            shareBtn.addClass('disabled');
        } else if(groupType===Dime.psMap.TYPE.PLACE){
            shareBtn.addClass('disabled');
        }else{
            shareBtn.removeClass('disabled');
        }
    },       

    updateMoreButton: function(groupType){
        var moreButton = $('#actionButtonMore');

         if(groupType===Dime.psMap.TYPE.SITUATION){
            moreButton.addClass('disabled');
        } else if(groupType===Dime.psMap.TYPE.PLACE){
            moreButton.addClass('disabled');
        }else{
            moreButton.removeClass('disabled');
        }

        //populate more dialog
        var createMenuItem = function(caption, callBack){
            var link= $('<a tabindex="-1" href="#" />')
                .text(caption)
                .clickExt(DimeView, callBack, DimeView.selectedItems);
            return $('<li/>').attr('role','menuitem').append(link);
        };

        var dropDownUl=$('#actionButtonDropDownMore');
        dropDownUl.empty();

        if (groupType===Dime.psMap.TYPE.GROUP){
            dropDownUl                
                .append(createMenuItem("Merge persons ..", function(event, jElement, selectedItems){
                    window.alert("Merging of selected items currently not supported!");
                })); 
        }
        dropDownUl
                .append(createMenuItem("Create new rule for selected ..", function(event, jElement, selectedItems){
                    window.alert("Create new rule for selected currently not supported!");
                }));

    },
            
    updateAddRemoveButton: function(groupType, viewType){
        var addRmvBtn = $('#actionButtonAddRemove');
        
        //set button to default
        addRmvBtn.empty().unbind("click").addClass("disabled");
        
        if(groupType===Dime.psMap.TYPE.PLACE){
            addRmvBtn
                    .empty()
                    .text("Get location ...")
                    .removeClass("disabled")
                    .click(function(){
                        if (navigator.geolocation) {
                            //getCurrentPosition() fires once - watchPosition() fires continuosly
                            //maybe: https://github.com/estebanav/javascript-mobile-desktop-geolocation
                            //maybe: http://dev.w3.org/geo/api/spec-source.html#get-current-position (enableHighAccuracy)
                            navigator.geolocation.getCurrentPosition(function(position) {                       
                                var lat = position.coords.latitude;
                                var lon = position.coords.longitude;
                                var acc = position.coords.accuracy;
                                Dime.psHelper.postCurrentContext(lat, lon, acc);
                                
                            }); 
                        }else{
                            (new Dime.Dialog.Toast("Geolocation services are not supported by your browser.")).show();
                        };
                        
                        DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.PLACE, DimeViewManager.GROUP_CONTAINER_VIEW, true);
                    });
        }
        
        if(viewType===DimeViewManager.PERSON_VIEW){
            addRmvBtn
                .empty()
                .text("Send Livepost ...")
                .removeClass("disabled")
                .click(function(){
                    //Dime.evaluation.createAndSendEvaluationItemForAction("action_sendLivepostFromPersonView");
                    var selectedPerson = JSTool.getDefinedMembers(DimeView.selectedItems);

                    var triggerDialog=function(response){
                        Dime.Dialog.showLivepostWithSelection(response);
                    };
                    Dime.psHelper.getMixedItems(selectedPerson, triggerDialog, this);
                });
        }
    },

    updateMetaBar: function(groupType){
        
        if (groupType===Dime.psMap.TYPE.GROUP){
            $('#metaDataShareAreaSharedWithCaption').text('Can access');
        }else{
            $('#metaDataShareAreaSharedWithCaption').text('Shared with');
        }
    },

   

    OrangeBubble: function(handlerSelf, caption, bubbleBody, dismissHandler){

        var bubbleSelf = this;

        this.bubbleId="Bubble_"+JSTool.randomGUID();

        //bubbleBody.addClass('modal-body');
        bubbleBody.addClass('bubble-body');

        var headerElement=
            $('<div></div>').addClass("modal-header")
            .append($('<button type="button" class="close" data-dismiss="modal" aria-hidden="true" >&lt&lt</button>')
                .click(function(){
                        bubbleSelf.dismiss.call(bubbleSelf);
                        dismissHandler.call(handlerSelf);
                    }
                ))
            .append($('<h3 id="myModalLabel">'+caption+'</h3>\n'));


        this.bubble= $('<div/>')
          //  .addClass('modal')
            .addClass('orangeBubble')
            .attr('id',this.bubbleId)
            .append(headerElement)
            .append(bubbleBody)
            ;
    },

    showAbout: function(){

        //if dialog is shown already - dismiss
        if (DimeView.bubble){
            DimeView.bubble.dismiss();
            DimeView.bubble=null;
            return;
        }
        var serverInfo = Dime.ps_configuration.serverInformation; //initially retrieved in main.js

        var loginbaselink=Dime.ps_configuration.getRealBasicUrlString()
        +'/dime-communications/web/access/';
        var githubLink='https://github.com/dime-project/meta/';
        var loginFromServerSettings=serverInfo.baseUrl+'/access/login';
        var questionaireLink = Dime.ps_configuration.getQuestionairePath();


        var bubbleBody = $('<div/>')
        .append(
            $('<div/>')
                .append($('<h3/>').text('Your feedback to the concept of di.me is very important for us!').css('font-size','16px'))
                .append($('<p/>')
                    .append($('<span/>').text('Please follow our')).css('margin-top','10px')
                    .addHrefOpeningInNewWindow('/dime-communications/static/ui/dime/howto.html','Guided Tour','orangeBubbleLink')
                    .append($('<span/>').text('... and get the mobile App'))
                    .addHrefOpeningInNewWindow('http://dimetrials.bdigital.org:8080/dimemobile.apk','for Android.','orangeBubbleLink')

                    
                    .css('font-size','16px')

                    )
                .append($('<h3/>').text('Please fill out our brief questionaire:')
                    .addHrefOpeningInNewWindow(questionaireLink+'?lang=en',' (English)','orangeBubbleLink')
                    .addHrefOpeningInNewWindow(questionaireLink+'?lang=de','(German)','orangeBubbleLink')
                    .css('font-size','16px').css('margin-bottom','30px')
                )
                

                .append($('<h3/>').text('This is a demonstration prototype'))
                .append($('<p/>')
                    .append($('<span/>').text('.. so you will find many bugs and issues.'))
                    .append($('<br/>')).append($('<span/>').text('Please report them on:'))
                    .addHrefOpeningInNewWindow(githubLink+'issues',githubLink+'issues','orangeBubbleLink')
                )
                .append($('<h3/>').text('About'))
                .append($('<p/>')
                    .append($('<span/>').text('The test trial homepage:'))
                    .addHrefOpeningInNewWindow('http://dimetrials.bdigital.org:8080/dime','dimetrials.bdigital.org','orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('di.me open source: comming soon'))
                    //.addHrefOpeningInNewWindow(githubLink,githubLink,'orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('The research project:'))
                    .addHrefOpeningInNewWindow('http://www.dime-project.eu','www.dime-project.eu','orangeBubbleLink')
                )
                .append($('<p/>')
                    .append($('<span/>').text('Your dime-server @ '+serverInfo.affiliation+":"))
                    .addHrefOpeningInNewWindow(loginFromServerSettings,serverInfo.baseUrl+"/[..]/login",'orangeBubbleLink')
                )
                .append($('<table/>')
                .append($('<tr/>')
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"conditions",'Usage Conditions (EN)','orangeBubbleLink'))
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"conditions?lang=de",'Nutzungsbedingungen (DE)','orangeBubbleLink'))
                    )
                .append($('<tr/>')
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"privacypolicy",'Privacy declaration (EN)','orangeBubbleLink'))
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"privacypolicy?lang=de",'Datenschutzerklärung (DE)','orangeBubbleLink'))
                )
                .append($('<tr/>')
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"about",'Imprint (EN)','orangeBubbleLink'))
                    .append($('<td/>')
                        .addHrefOpeningInNewWindow(loginbaselink+"about?lang=de",'Impressum (DE)','orangeBubbleLink'))

                ))
        );

        DimeView.bubble = new DimeView.OrangeBubble(this,'Welcome and many thanks for trying out di.me!',  bubbleBody, function(){
            //dismiss handler
            DimeView.bubble=null;
        });
        DimeView.bubble.show();


    }//END DimeView.showAbout()
};

DimeView.OrangeBubble.prototype = {

    show: function(){
        $('body').append(this.bubble);
    },
    dismiss: function(){
        $(this.bubble).remove();
    }
};

DimeView.viewManager = new DimeViewManager();

//---------------------------------------------
//#############################################
//  DimeView - END
//#############################################
//



//---------------------------------------------
//#############################################
//  Dime.Settings
//#############################################
//


Dime.Settings = {
    evaluationInfoHtml:'<span>The following data will be collected for evaluation:</span><br><ul><li>an anonymous identifyier which allows us to know what click data and questionaire answers come from the same account. No other identity information like your di.me username, nickname, real name, or email-address is sent. No location information is sent.</li><li>statistics about how many contacts, files, messages, and connected systems you use in your system. Only the number and time when created, but no information about names, content, or anything else is sent.</li><li>data about what type of pages you click in the system (e.g. a page „person“). This includes the time the page was clicked. No title, text, or other content of the pages are sent.</li></ul><br/><span>We do not use other click analysis (like e.g. Google Analytics). You can switch this off at any time on the page "Settings".</span>',

    //in segovia some services have been hidden
    //hiddenServices: ['SocialRecommenderServiceAdapter', 'AMETICDummyAdapter', 'Facebook'],
    hiddenServices: [],

    createServiceAccountElement: function(item) {
        
        return $('<div title="' + item.name + '"></div>')
                .addClass("wrapConnect")
                .clickExt(Dime.Settings, Dime.Settings.editServiceAccount, item)
                .append(
                    $('<img></img>')                    
                    .attr("src", Dime.psHelper.guessLinkURL(item.imageUrl))                    
                   )
                .append("<b>" + DimeView.getShortNameWithLength(item.name, 22) + "</b></br>")
                .append(
                    $("<span></span>")
                    .addClass("serviceActiveMessage")
                    .append("[active]")
                    );
    },

    getAdapterByGUID: function(guid, callback) {

        Dime.REST.getItem(guid, Dime.psMap.TYPE.SERVICEADAPTER, callback, "@me", Dime.Settings);
    },

    initServiceAdapters: function(adapters) {
        
        var dropdownList = $("#addNewServiceAdapterDropDown").empty();

        for (var i = 0; i < adapters.length; i++) {

            //hide adapter if in Dime.Settings.hiddenServices
            var hideAdapter = false;
            for (var j = 0; j < Dime.Settings.hiddenServices.length; j++) {
                if (adapters[i].name === Dime.Settings.hiddenServices[j]) {
                    hideAdapter = true;
                }
            }
            if (hideAdapter) {
                continue;
            }
            //end hide

            //append service adapter to dropdown
            var dropItem = $("<li></li>").attr("role", "menuitem")
            .append(
                $('<a tabindex="-1" target="_blank"  id="'
                    + adapters[i].guid + '">' + adapters[i].name.substring(0, 16) + '</a>')
                //DimeView.getShortNameWithLength(adapters[i].name, 16)
                .clickExt(this, Dime.Settings.dropdownOnClick, adapters[i])
                .append('<img class="serviceAdapterIconDropdown" src="' + adapters[i].imageUrl + '" width="20px" align="middle" />')
                );
            dropdownList.append(dropItem);
        }

    },

    initServiceAccounts: function(accounts) {

        var serviceContainer=$('#serviceContainer').empty();
        

        for (var i = 0; i < accounts.length; i++) {

            //hide adapter if in Dime.Settings.hiddenServices
            var hideAdapter = false;
            for (var j = 0; j < Dime.Settings.hiddenServices.length; j++) {
                if (accounts[i].name === Dime.Settings.hiddenServices[j]) {
                    hideAdapter = true;
                }
            }
            if (hideAdapter) {
                continue;
            }
            //end hide
            serviceContainer.append(Dime.Settings.createServiceAccountElement(accounts[i]));

        }
        serviceContainer.append($('<div/>').addClass('clear'));
    },

    editServiceAccount: function(event, element, item){
        var serviceAdapterCallback=function(serviceAdapter){
            var dialog = new Dime.ConfigurationDialog(this, this.configurationSubmitHandler);
            dialog.show(serviceAdapter.name,serviceAdapter.description, item, false);
        };

        Dime.Settings.getAdapterByGUID(item.serviceadapterguid, serviceAdapterCallback);
        
    },

    deactivateServiceAccount: function(event, element, serviceAccount) {
        $("#lightBoxBlack").fadeOut(300);
        $('#ConfigServiceDialogID').remove();
        var callBackHandler = function(response) {
            console.log("DELETED service " + serviceAccount.guid + " - response:", response);
        };

        Dime.REST.removeItem(serviceAccount, callBackHandler, Dime.Settings);
    },

    toggleTab: function(element, containerId) {
        if (!containerId || !containerId.length === 0 || !element) {
            return;
        }

        var containerElement = $('#'+containerId);

        if (containerElement.hasClass("expandedSettingsTab")) {
            //collapse
            containerElement.removeClass("expandedSettingsTab");
            containerElement.addClass("collapsedSettingsTab");
            element.setAttribute("src", "img/settings/open.png");
        } else {
            //expand
            containerElement.removeClass("collapsedSettingsTab");
            containerElement.addClass("expandedSettingsTab");
            element.setAttribute("src", "img/settings/collaps.png");
        }
    },

    updateView: function(){
        Dime.Settings.updateSettings();
        Dime.Settings.updateServices();
        Dime.Settings.updateAccounts();
    },


    updateSettings: function(){
       
        var self = this;
        var handleUserSettings=function(user){
            var evaluationCheckbox=function(){
                var checkboxId=JSTool.randomGUID();

                var input = $('<input/>')
                    .attr('id',checkboxId)
                    .attr('type','checkbox')
                    .prop("checked", user.evaluationDataCapturingEnabled)
                    .css('clear', 'both')
                    .click(function(){
                        //update user
                        user.evaluationDataCapturingEnabled=input.prop("checked");
                        Dime.ps_configuration.userInformation = user; //direct update settings - will be refreshed from update user with the callback
                        Dime.REST.updateUser(user,Dime.Settings.updateSettings, Dime.Settings);
                        if (user.evaluationDataCapturingEnabled){
                            (new Dime.Dialog.Toast("Send evaluation-data: activated")).show();
                        }else{
                            (new Dime.Dialog.Toast("Send evaluation-data: deactivated")).show();
                        }
                    });
                    
                $(this)
                    .append($('<div/>').text("Evaluation:").css('float', 'left'))
                    .append(Dime.Dialog.Helper.getInfoBox(self.evaluationInfoHtml, "evaluationInfoBox", "evaluationInfoIcon"))
                    .append(input)
                    .append($('<label/>')
                        .attr('for',checkboxId)
                        .text('send evaluation-data')
                        .css('width', '186px').css('float', 'right')
                );
            };

            var changePasswordButton=function(){
                var myPass=null;
                var passDlgShown=false;
                var myContainer = $(this);
                var input= $('<button/>').addClass('YellowMenuButton').text("Change Password")
                    .click(function(){
                        if (passDlgShown){
                            myContainer.find('.settingsPasswdField').remove();
                            passDlgShown=false;
                            myPass=null;
                            return;
                        }//else
                        passDlgShown=true;
                        myContainer.append($('<div/>').addClass('settingsPasswdField')
                            .append($('<input/>').attr('type','password').attr('placeholder','enter new password')
                                .keyup(function(event){
                                    if (event.keyCode == 13) {
                                        if (!myPass){//first time
                                            myPass=$(this).val();
                                            $(this).attr('placeholder','enter password again').val("");
                                        }else{ //retyped
                                            var secondPass = $(this).val();
                                            if (secondPass!==myPass){                                            
                                                (new Dime.Dialog.Toast("Please try again! - Your passwords didn't match!")).showLong();
                                            }else{
                                                user.password=myPass;
                                                Dime.REST.updateUser(user, Dime.Settings.updateSettings, Dime.Settings);
                                                (new Dime.Dialog.Toast("Password updated successfully!")).show();
                                            }
                                            myPass=null;
                                            myContainer.find('.settingsPasswdField').remove();
                                        }
                                    }
                                })
                        )
                        );
                        });
                myContainer.append(input);
            };


            var container = $('#generalSettingsContainer');
            container.empty();
            container
                .append($('<div/>').addClass('settingsSettings').append(evaluationCheckbox))
                .append($('<div/>').addClass('settingsSettings').append(changePasswordButton))
            ;
        };
        Dime.REST.getUser(handleUserSettings, Dime.Settings);
    },

    updateServices: function() {
                
        var callback = function(response) {
            Dime.Settings.initServiceAdapters(response);
        
        };
        
        Dime.REST.getAll(Dime.psMap.TYPE.SERVICEADAPTER, callback , '@me', Dime.Settings);

    },

    updateAccounts: function() {

        var callback = function(response) {
            Dime.Settings.initServiceAccounts(response);
        
        };

        Dime.REST.getAll(Dime.psMap.TYPE.ACCOUNT, callback , '@me', Dime.Settings);
    },

    createAccount: function(serviceAdapter) {
        var newAccount = Dime.psHelper.createNewItem(Dime.psMap.TYPE.ACCOUNT, serviceAdapter.name+"_account");
        newAccount.imageUrl=serviceAdapter.imageUrl;
        newAccount.serviceadapterguid = serviceAdapter.guid;
        //deep copy of settings
        var clonedArray = $.map(serviceAdapter.settings, function(obj){
            return $.extend(true, {}, obj);
        });
        newAccount.settings = clonedArray;
        return newAccount;
    },

    dropdownOnClick: function(event, jqueryItem, serviceAdapter) {
        
        if (serviceAdapter.authUrl) {
            //showing service description before open in new window
            var dialog = new Dime.ConfigurationDialog(this, this.configurationSubmitHandler);
            dialog.showAuth(serviceAdapter.name, serviceAdapter.description, serviceAdapter.authUrl);
        } else {
            //create account item
            var newAccountItem = Dime.Settings.createAccount(serviceAdapter);
            var dialog = new Dime.ConfigurationDialog(this, this.configurationSubmitHandler);
            dialog.show(serviceAdapter.name, serviceAdapter.description, newAccountItem, true);
        }
    },

    configurationSubmitHandler: function(serviceAccount, isNewAccount) {
        var callBack;

        if(isNewAccount){
            callBack = function(response) {
                console.log("NEW ACCOUNT: ", response);
                if (!response|| response.length<1){
                    (new Dime.Dialog.Toast("Creation of "+serviceAccount.name+" failed!")).show();
                }else{
                    (new Dime.Dialog.Toast(serviceAccount.name+ " created successfully.")).show();
                }
            };
            Dime.REST.postNewItem(serviceAccount, callBack);
        }else{
            callBack = function(response) {
                console.log("ACCOUNT UPDATED: ", response);
                if (!response || response.length<1){
                    (new Dime.Dialog.Toast("Updating "+serviceAccount.name+" failed!")).show();
                }else{
                    (new Dime.Dialog.Toast(serviceAccount.name+ " updated successfully.")).show();
                }
            };
            Dime.REST.updateItem(serviceAccount, callBack);
        }
    }
};


//---------------------------------------------
//#############################################
//  Dime.Settings - END
//#############################################
//---------------------------------------------


//---------------------------------------------
//#############################################
//  Dime.initProcessor
//#############################################
//---------------------------------------------


/**
 * init page
 * 
 * handler for URL-parameter
 */
Dime.initProcessor.registerFunction(function(callback){ 
    
    $('#contentContainer').mouseoutExt(DimeView, DimeView.hideMouseOver);
    
    
    //set event listeners to group container
    $('#groupNavigation').click(function(){
        //reset search text
        
       DimeView.search(); 
    });
    
    //set grouptype
    var groupType = Dime.psHelper.getURLparam("type");
    var viewType = Dime.psHelper.getURLparam("viewType");
    var guid = Dime.psHelper.getURLparam("guid");
    var dItemType = Dime.psHelper.getURLparam("dItemType");
    var userId = Dime.psHelper.getURLparam("userId");
    var message = Dime.psHelper.getURLparam("msg");
    
    DimeView.viewManager.updateViewExplicit.call(DimeView.viewManager, groupType, viewType, guid, dItemType, userId, message);
    
    callback();
});

/**
 * show info page on first login
 *
 * handler for URL-parameter
 */
Dime.initProcessor.registerFunction(function(callback){
    var getUserCallback=function(response){
        if (!response){
            return;
        }
        if (response.userStatusFlag===0){            
            //update user status
            response.userStatusFlag=1;
            Dime.REST.updateUser(response);
            DimeView.showAbout();
        }
    };
    Dime.REST.getUser(getUserCallback, DimeView);

    callback();
});


/**
 * show info page on first login
 *
 * handler for URL-parameter
 */
Dime.initProcessor.registerFunction(function(callback){
    var getUsernotificationCallback=function(userNotifications){
        var unReadUNs=[];
        jQuery.each(userNotifications, function(){
           if (!this.read){
               unReadUNs.push(this);
           }
        });
        Dime.Navigation.updateNotificationBar(unReadUNs);        
    };
    Dime.REST.getAll(Dime.psMap.TYPE.USERNOTIFICATION, getUsernotificationCallback, "@me", this);
    
    callback();
});

//---------------------------------------------
//#############################################
//  Dime.initProcessor - END
//#############################################
//---------------------------------------------
//---------------------------------------------
//#############################################
//  Dime.Navigation - override
//#############################################
//---------------------------------------------
Dime.Navigation.updateView = function(notifications){
    DimeView.viewManager.updateViewFromNotifications.call(DimeView.viewManager, notifications);
};

Dime.Navigation.createMenuLiButton=function(id, caption, containerGroupType){

    return $('<li/>').attr('id',id).append($('<a/>')
        .click(function(){
            //update view
            DimeView.viewManager.updateView.call(DimeView.viewManager, containerGroupType, DimeViewManager.GROUP_CONTAINER_VIEW);
        })
        .text(caption));
};

Dime.Navigation.createMenuLiButtonSettings=function(){
    return $('<li/>').attr('id','navButtonSettings').append($('<a/>')
        .click(function(){
            //update view
            DimeView.viewManager.updateView.call(DimeView.viewManager, "", DimeViewManager.SETTINGS_VIEW);
        })
        .text('Settings'));

};


Dime.Navigation.createNotificationIcon=function(){
    return $('<div/>').addClass('notificationIcon').attr('id','notificationIcon')
        .click(function(){
            //update view
            DimeView.viewManager.updateView.call(DimeView.viewManager, Dime.psMap.TYPE.USERNOTIFICATION, DimeViewManager.GROUP_CONTAINER_VIEW);
        })
        .append($('<div/>').attr('id','notificationCounter').text("0"));
};

//---------------------------------------------
//#############################################
//  Dime.Navigation - override - END
//#############################################
//---------------------------------------------

/**
 * handle back button --- FIXME not working as intended
 */
 window.onpopstate = function(event) {
     var viewState = event.state;
     console.log(viewState);
     if (viewState){
         DimeView.viewManager.updateView.call(DimeView.viewManager, viewState.groupType, viewState.viewType, true);
     }
};