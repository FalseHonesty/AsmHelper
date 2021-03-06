// For 1.15.2+
function initializeCoreMod() {
    var asmHelper = Java.type("dev.falsehonesty.asmhelper.AsmHelper")

    return {
        asmhelper: {
            target: {
                type: "CLASS",
                names: function() {
                    var classNamesArrayList = asmHelper.classNames();

                    // Nashorm doesn't automatically convert ArrayLists
                    // to ScriptObjectMirror, so we have to create a JS
                    // array ourselves
                    var classNames = [];

                    for (var i = 0; i < classNamesArrayList.size(); i++)
                        classNames.push(classNamesArrayList.get(i));

                    return classNames;
                }
            },
            transformer: function(classNode) {
                asmHelper.transform(classNode);
                return classNode;
            }
        }
    }
}
