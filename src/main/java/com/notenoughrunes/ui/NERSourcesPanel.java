package com.notenoughrunes.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notenoughrunes.types.BooleanTypeAdapter;
import com.notenoughrunes.types.NERData;
import com.notenoughrunes.types.NERProductionRecipe;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.RuneLiteAPI;

class NERSourcesPanel extends JPanel
{
	private static final Gson gson = RuneLiteAPI.GSON.newBuilder().registerTypeAdapter(boolean.class, new BooleanTypeAdapter()).create();

	NERSourcesPanel(NERItem nerItem, ItemManager itemManager, NERData nerData, ClientThread clientThread)
	{
		setLayout(new BorderLayout());

		String recipeString = "{\"ticks\":\"1\",\"materials\":[{\"name\":\"Pure essence\",\"quantity\":\"26\"},{\"name\":\"Fire rune\",\"quantity\":\"26\"},{\"name\":\"Fire talisman\",\"quantity\":\"1\"}],\"facilities\":\"Earth altar\",\"tools\":\"Binding necklace, Magic Imbue\",\"skills\":[{\"experience\":\"10\",\"level\":\"23\",\"name\":\"Runecraft\",\"boostable\":\"yes\"}],\"members\":\"Yes\",\"output\":{\"quantitynote\":\"50% chance of success per essence, or<br/> 100% chance of success when wearing a [[Binding necklace]].\",\"cost\":3,\"quantity\":\"13\",\"name\":\"Lava rune\",\"subtxt\":\"Earth altar\",\"image\":\"[[File:Lava rune.png|link=Lava rune]]\"}}";
		NERProductionRecipe recipe = gson.fromJson(recipeString, new TypeToken<NERProductionRecipe>() {}.getType());

		JPanel wrapper = new JPanel(new BorderLayout());

		NERRecipePanel recipePanel = new NERRecipePanel(recipe, itemManager, nerData, clientThread);
		wrapper.add(recipePanel, BorderLayout.NORTH);


		add(wrapper, BorderLayout.NORTH);
	}
}
