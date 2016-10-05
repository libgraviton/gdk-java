package com.github.libgraviton.gdk.generator;

import java.util.List;

/**
 * Defines generator instruction loaders. These are components capable of providing instructions which can be
 * processed by the generator.
 */
public interface GeneratorInstructionLoader {

    /**
     * Loads all generator instructions. If the instructions are already loaded, a cached instruction set should be
     * returned.
     *
     * @return The instruction set.
     */
    public List<GeneratorInstruction> loadInstructions();

    /**
     * Loads all generator instructions.
     *
     * @param reload If the instructions are already loaded and this param is set to false, a cached instruction set
     *               should be returned. Otherwise the instruction list should be (re-) loaded.
     *
     * @return The instruction set.
     */
    public List<GeneratorInstruction> loadInstructions(boolean reload);

}
