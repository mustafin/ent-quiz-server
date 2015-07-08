/**
 * Created by Murat on 05.07.2015.
 */
//to use add names []


var conf = {
    counter : 0,
    names : []
};

var DynamicForm = function(options){
    if(options) conf = options;

    this.dfElem = $(".df-temp").first();
    this.dfElem.remove();

    console.log("asfrgr");
    this.genEl = function(name){
        console.log(conf.counter);
        return name.replace("%d", conf.counter)
    };

    $('.df-container input[type=checkbox]').change(function() {
        $('.df-container input[type=checkbox]:checked').not(this).prop('checked', false);
    });
};

DynamicForm.prototype.addElement = function(){

    var form = this;

    var elToAdd = form.dfElem.clone();

    conf.names.forEach(function(name, i){
        var curField = elToAdd.find(".df-field")[i];
        $(curField).attr("name", form.genEl(name))
    });

    $(".df-container").append(elToAdd);

    conf.counter++;

    $('.df-container input[type=checkbox]').change(function() {
        $('.df-container input[type=checkbox]:checked').not(this).prop('checked', false);
    });

    elToAdd.find('.df-close').click(function(){
        conf.counter--;
        elToAdd.remove();
    });
};


