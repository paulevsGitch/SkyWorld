package paulevs.skyworld;

import com.mojang.datafixers.Dynamic;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import paulevs.skyworld.generator.SkyWorldChunkGeneratorConfig;

public class CustomizeSkyWorldScreen extends Screen
{
	private CreateWorldScreen parent;
	private SkyWorldChunkGeneratorConfig config;
	private int dist = 6;
	
	private ButtonListWidget list;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CustomizeSkyWorldScreen(CreateWorldScreen parent, CompoundTag generatorOptions)
	{
		super(new TranslatableText("createWorld.customize.flat.title", new Object[0]));
		this.parent = parent;
		this.config = SkyWorldChunkGeneratorConfig.fromDynamic(new Dynamic(NbtOps.INSTANCE, config));
		if (generatorOptions.contains("distance"))
			dist = generatorOptions.getInt("distance");
	}

	protected void init()
	{
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), (buttonWidget) -> {
			openScreenAndEnableButton();
		}));

		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), (buttonWidget) -> {
			openScreenAndEnableButton();
		}));
		
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.list.addSingleOptionEntry(new DoubleOption("options.skyworld.distance", 2.0D, 32.0D, 1.0F, (gameOptions) -> {
			return (double) dist;
		}, (gameOptions, var1) -> {
			dist = (int) Math.round(var1);
			config.setIslandDistance(dist);
			this.parent.generatorOptionsTag.putInt("distance", dist);
		}, (gameOptions, doubleOption) -> {
			return String.format("%s [%s: %d] [%s: %d]",
					I18n.translate("createWorld.customize.skyworld.islanddistance"),
					I18n.translate("createWorld.customize.skyworld.inchunks"),
					dist,
					I18n.translate("createWorld.customize.skyworld.inblocks"),
					dist * 16);
		}));
		this.children.add(this.list);

		/*new DoubleOption("options.skyworld.distance", 2.0D, 32.0D, 1.0F, (gameOptions) -> {
			return (double) dist;
		}, (gameOptions, var1) -> {
			dist = (int) Math.round(var1);
			config.setIslandDistance(dist);
			this.parent.generatorOptionsTag.putInt("distance", dist);
		}, (gameOptions, doubleOption) -> {
			return String.format("%s [%s: %d] [%s: %d]",
					I18n.translate("createWorld.customize.skyworld.islanddistance"),
					I18n.translate("createWorld.customize.skyworld.inchunks"),
					dist,
					I18n.translate("createWorld.customize.skyworld.inblocks"),
					dist * 16);
		}).createButton(this.minecraft.options, 85, 20, 310);*/
	}

	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}

	public CompoundTag getConfigTag()
	{
		return (CompoundTag) this.config.toDynamic(NbtOps.INSTANCE).getValue();
	}
	
	protected void openScreenAndEnableButton()
	{
		this.minecraft.openScreen(this.parent);
		if (SkyWorld.BUTTON[0] != null)
			SkyWorld.BUTTON[0].visible = true;
	}
}
