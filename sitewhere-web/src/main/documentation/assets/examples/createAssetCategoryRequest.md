The category id should be a unique value not already being used by another module. The system will allow
a category to be created with an id that matches an existing module. In this case, when the system creates
an asset module for the category, it will mask the existing module. This is allowed for purposes of 
testing when systems for external asset modules are not available. Category ids must be alphanumeric and
can contain dashes or underscores.