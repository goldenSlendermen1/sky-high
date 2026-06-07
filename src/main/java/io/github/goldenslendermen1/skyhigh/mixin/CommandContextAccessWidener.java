package io.github.goldenslendermen1.skyhigh.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import io.github.goldenslendermen1.skyhigh.helper.WidenedCommandContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(CommandContext.class)
public class CommandContextAccessWidener<S> implements WidenedCommandContext<S> {
	@Shadow
	@Final
	private Map<String, ParsedArgument<S, ?>> arguments;

	@Unique
    public Map<String, ParsedArgument<S, ?>> sky_high$getArguments() {
		return arguments;
	}
}