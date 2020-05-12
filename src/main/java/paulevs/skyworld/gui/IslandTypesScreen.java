package paulevs.skyworld.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import paulevs.skyworld.structures.generators.Generators;

public class IslandTypesScreen extends Screen
{
	private Screen parrent;
	private ButtonListWidget list;
	private Map<String, Boolean> types;
	private CompoundTag generatorOptions;
	
	protected IslandTypesScreen(Screen parrent, CompoundTag generatorOptions)
	{
		super(parrent.getTitle());
		this.parrent = parrent;
		this.types = new HashMap<String, Boolean>();
		this.generatorOptions = generatorOptions;
		
		for (String type: Generators.getGenerators())
		{
			boolean hasIsland = true;
			String key = "island_" + type;
			if (generatorOptions.contains(key))
				hasIsland = generatorOptions.getBoolean(key);
			types.put(type, hasIsland);
		}
	}
	
	@Override
	protected void init()
	{
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), (buttonWidget) -> {
			onClose();
		}));

		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), (buttonWidget) -> {
			onClose();
		}));
		
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		
		for (String type: Generators.getGenerators())
		{
			this.list.addSingleOptionEntry(new BooleanOption(
					"options.skyworld.type." + type,
					(gameOptions) -> {
						return types.getOrDefault(type, true);
					}, (gameOptions, var1) -> {
						types.put(type, var1);
						generatorOptions.putBoolean("island_" + type, var1);
					}));
		}
		
		this.children.add(this.list);
	}

	@Override
	public void onClose()
	{
		this.minecraft.openScreen(parrent);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
}