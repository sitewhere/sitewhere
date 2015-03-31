<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/i18next-1.7.7.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/i18next.amd.withJQuery-1.7.7.min.js"></script>
<script type="text/javascript">
   var i18next;
   var sitewhere_i18next = {
                             "sitewhere_title":"",
                             "title_arry":[],
                             "i18nTitleLable":function(){
                                 var arry = this.title_arry;
                                 for(var i=0;i<arry.length;i++){
		                            $("input[title='"+arry[i].key+"']").attr("title",i18next(arry[i].value));
	                             }
                             },
                             "i18n_titleProperties":function(){
                                 var title = i18next(this.sitewhere_title);
                                 if((title != null) || title != ""){
                                     $("title").html("SiteWhere - " + title);
                                 }
                             },
                             "i18n":function(){
                                 this.i18nTitleLable();
                                 this.i18n_titleProperties();
                             }
                           };
                           
   /** Get lng */                 
   var lng = window.navigator.language;
   if(lng != null && lng !=""){
      lng = lng.substring(0,2).toLowerCase();
   }else{
      lng = "dev";
   }
   
    /** I18n init */ 
   $(function(){
      i18nInit();
   });
   
   /** I18n init function */
   function i18nInit(){
      var option = {lng:lng,resGetPath:'${pageContext.request.contextPath}/locales/__lng__/translation.json',useLocalStorage:true,lowerCaseLng:true};
      i18n.init(option,function(t){
         $("body").i18n(); 
         i18next = t;
         /** I18n sitewhere (properties and tile lab)*/
         sitewhere_i18next.i18n();
      });
   }
   
   /** Arry Accumulate */
   function arryAccum(arry1,arry2){
       var tem_arry = [];
       var end = arry1.length;;
       for(var i=0;i<arry1.length;i++){
          tem_arry[i] = arry1[i];
       }
       for(var i=0;i<arry2.length;i++){
          tem_arry[end + i] = arry2[i];
       }
       return tem_arry;
   }
</script>