package com.BankAdvisor;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BankAdvisorPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BankAdvisorPlugin.class);
		RuneLite.main(args);
	}
}