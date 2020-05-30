package paulevs.skyworld.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import paulevs.skyworld.SkyWorld;

public class CustomizeSkyWorldScreen extends Screen
{
	private CreateWorldScreen parent;
	private CompoundTag generatorOptions;
	private int dist = 6;
	private int biomeSize = 3;
	private boolean hasLeafVines = true;
	private boolean hasNormalVines = true;
	private boolean hasBushes = true;
	private boolean hasOcean = false;
	
	private ButtonListWidget list;

	public CustomizeSkyWorldScreen(CreateWorldScreen parent, CompoundTag generatorOptions)
	{
		super(new TranslatableText("createWorld.customize.flat.title", new Object[0]));
		this.parent = parent;
		this.generatorOptions = generatorOptions;
		this.parent.generatorOptionsTag = generatorOptions;
		
		if (generatorOptions.contains("distance"))
			dist = generatorOptions.getInt("distance");
		if (generatorOptions.contains("biomesize"))
			dist = generatorOptions.getInt("biomesize");
		if (generatorOptions.contains("hasleafvines"))
			hasLeafVines = generatorOptions.getBoolean("hasleafvines");
		if (generatorOptions.contains("hasnormalvines"))
			hasNormalVines = generatorOptions.getBoolean("hasnormalvines");
		if (generatorOptions.contains("hasbushes"))
			hasBushes = generatorOptions.getBoolean("hasbushes");
		if (generatorOptions.contains("hasocean"))
			hasOcean = generatorOptions.getBoolean("hasocean");
	}

	@Override
	protected void init()
	{
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), (buttonWidget) -> {
			openScreenAndEnableButton();
		}));

		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), (buttonWidget) -> {
			openScreenAndEnableButton();
		}));
		
		this.addButton(new ButtonWidget(this.width / 2 - 155, 8, 150, 20, I18n.translate("createWorld.customize.skyworld.islands"), (buttonWidget) -> {
			this.minecraft.openScreen(new IslandTypesScreen(this, generatorOptions));
		}));
		
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		
		this.list.addSingleOptionEntry(new DoubleOption("options.skyworld.distance", 2.0D, 32.0D, 1.0F, (gameOptions) -> {
			return (double) dist;
		}, (gameOptions, var1) -> {
			dist = (int) Math.round(var1);
			generatorOptions.putInt("distance", dist);
		}, (gameOptions, doubleOption) -> {
			return String.format("%s [%s: %d] [%s: %d]",
					I18n.translate("createWorld.customize.skyworld.islanddistance"),
					I18n.translate("createWorld.customize.skyworld.inchunks"),
					dist,
					I18n.translate("createWorld.customize.skyworld.inblocks"),
					dist * 16);
		}));
		
		this.list.addSingleOptionEntry(new DoubleOption("options.skyworld.biomesize", 1.0D, 16.0D, 1.0F, (gameOptions) -> {
			return (double) biomeSize;
		}, (gameOptions, var1) -> {
			biomeSize = (int) Math.round(var1);
			generatorOptions.putInt("biomesize", biomeSize);
		}, (gameOptions, doubleOption) -> {
			return String.format("%s: %d",
					I18n.translate("createWorld.customize.skyworld.biomesize"),
					biomeSize);
		}));

		this.list.addSingleOptionEntry(new BooleanOption(
				"options.skyworld.hasleafvines",
				(gameOptions) -> {
					return hasLeafVines;
				}, (gameOptions, var1) -> {
					hasLeafVines = var1;
					generatorOptions.putBoolean("hasleafvines", hasLeafVines);
				}));
		
		this.list.addSingleOptionEntry(new BooleanOption(
				"options.skyworld.hasnormalvines",
				(gameOptions) -> {
					return hasNormalVines;
				}, (gameOptions, var1) -> {
					hasNormalVines = var1;
					generatorOptions.putBoolean("hasnormalvines", hasNormalVines);
				}));
		
		this.list.addSingleOptionEntry(new BooleanOption(
				"options.skyworld.hasbushes",
				(gameOptions) -> {
					return hasBushes;
				}, (gameOptions, var1) -> {
					hasBushes = var1;
					generatorOptions.putBoolean("hasbushes", hasBushes);
				}));
		
		this.list.addSingleOptionEntry(new BooleanOption(
				"options.skyworld.hasocean",
				(gameOptions) -> {
					return hasOcean;
				}, (gameOptions, var1) -> {
					hasOcean = var1;
					generatorOptions.putBoolean("hasocean", hasOcean);
				}));

		this.children.add(this.list);
	}

	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
	
	protected void openScreenAndEnableButton()
	{
		this.minecraft.openScreen(this.parent);
		if (SkyWorld.button_customize != null)
			SkyWorld.button_customize.visible = true;
	}
}
