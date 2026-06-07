package io.github.goldenslendermen1.skyhigh.helper;

import com.mojang.brigadier.context.ParsedArgument;

import java.util.Map;

public interface WidenedCommandContext<S> {
    Map<String, ParsedArgument<S, ?>> sky_high$getArguments();
}
