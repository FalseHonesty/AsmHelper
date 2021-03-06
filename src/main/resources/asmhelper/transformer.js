// For 1.15.2+
function initializeCoreMod() {
    print("[JS] initializeCoreMod")
    var asmHelper = Java.type("me.falsehonesty.asmhelper.AsmHelper")

    return {
        asmhelper: {
            target: {
                type: "CLASS",
                names: function() {
                    var classNamesArrayList = asmHelper.classNames();
                    print("[JS] names()");

                    // Nashorm doesn't automatically convert ArrayLists
                    // to ScriptObjectMirror, so we have to create a JS
                    // array ourselves
                    var classNames = [];

                    for (var i = 0; i < classNamesArrayList.size(); i++) {
                        print("[JS] classname: " + classNamesArrayList.get(i));
                        classNames.push(classNamesArrayList.get(i));
                    }

                    return classNames;
                }
            },
            transformer: function(classNode) {
                print("[JS] Transforming " + classNode.name);
                asmHelper.transform(classNode);
                return classNode;
            }
        }
    }
}
