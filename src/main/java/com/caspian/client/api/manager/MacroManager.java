package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.macro.Macro;
import com.caspian.client.api.handler.MacroHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Macro
 */
public class MacroManager
{
    // The handler for handling macros
    private final MacroHandler macroHandler;

    //
    private final Set<Macro> macros = new HashSet<>();

    /**
     *
     *
     */
    public MacroManager()
    {
        macroHandler = new MacroHandler();
        Caspian.EVENT_HANDLER.subscribe(macroHandler);
    }

    /**
     * Loads macros from the file system
     */
    public void postInit()
    {
        // TODO
    }

    /**
     *
     *
     * @param macros
     */
    public void register(Macro... macros)
    {
        for (Macro macro : macros)
        {
            register(macro);
        }
    }

    /**
     *
     *
     * @param macro
     */
    public void register(Macro macro)
    {
        macros.add(macro);
    }

    /**
     *
     *
     * @return
     */
    public Collection<Macro> getMacros()
    {
        return macros;
    }
}
