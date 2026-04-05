package it.crystalnest.fancy_entity_renderer.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(HolderSet.Named.class)
public interface HolderSetNamedAccessor<T> {
  @Invoker("<init>")
  static <T> HolderSet.Named<T> fer$create(HolderOwner<T> owner, TagKey<T> key) {
    throw new AssertionError();
  }

  @Invoker("bind")
  void fer$bind(List<Holder<T>> contents);
}
