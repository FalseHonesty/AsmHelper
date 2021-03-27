package me.falsehonesty.asmhelpermod.core;

import me.falsehonesty.asmhelpermod.AsmHelperMod;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class AsmHelperLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "me.falsehonesty.asmhelper.example.TestClassTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return AsmHelperMod.class.getName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
