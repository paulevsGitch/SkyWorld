package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelGeneratorType;
import paulevs.skyworld.SkyWorld;
import paulevs.skyworld.SkyWorldType;
import paulevs.skyworld.gui.CustomizeSkyWorldScreen;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen
{
	@Shadow
	private ButtonWidget customizeTypeButton;
	
	@Shadow
	private int generatorType;
	
	@Shadow
	public CompoundTag generatorOptionsTag;
	
	@Shadow
	private boolean moreOptionsOpen;
	
	protected CreateWorldScreenMixin(Text title)
	{
		super(title);
	}
	
	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfo info)
	{
		CreateWorldScreen self = (CreateWorldScreen) (Object) this;
		SkyWorld.button_customize = (ButtonWidget) addButton(new ButtonWidget(self.width / 2 + 5, 120, 150, 20, I18n.translate("selectWorld.customizeType"), (buttonWidget) -> {
			if (LevelGeneratorType.TYPES[this.generatorType] == SkyWorldType.SKY_WORLD)
			{
				this.minecraft.openScreen(new CustomizeSkyWorldScreen(self, generatorOptionsTag));
			}
		}));
		SkyWorld.button_customize.visible = moreOptionsOpen && (LevelGeneratorType.TYPES[this.generatorType] == SkyWorldType.SKY_WORLD);;
	}
	
	@Inject(method = "setMoreOptionsOpen", at = @At("HEAD"))
	private void toggleVisibility(boolean moreOptionsOpen, CallbackInfo info)
	{
		if (SkyWorld.button_customize != null)
			SkyWorld.button_customize.visible = moreOptionsOpen && LevelGeneratorType.TYPES[this.generatorType] == SkyWorldType.SKY_WORLD;
	}
}