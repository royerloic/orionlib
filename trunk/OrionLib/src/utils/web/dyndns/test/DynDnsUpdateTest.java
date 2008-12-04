package utils.web.dyndns.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import utils.web.dyndns.DynDnsUpdater;
import utils.web.dyndns.JddUpdateException;

public class DynDnsUpdateTest
{
	// @Test
	public void testDynDnsUpdate() throws InterruptedException
	{
		Random rnd = new Random();

		Scanner sc = new Scanner(System.in);
		String login = sc.nextLine();
		String password = sc.nextLine();
		String host = sc.nextLine();
		String ip = "192.192.192." + Math.abs(rnd.nextInt() % 255);
		System.out.println("ip is: " + ip);

		DynDnsUpdater updater = new DynDnsUpdater("", login, password);
		try
		{
			updater.update(host, ip);
		}
		catch (JddUpdateException e)
		{
			e.printStackTrace();
			fail();
		}

		Thread.sleep(10000);

		InetAddress inet;
		try
		{
			inet = InetAddress.getByName(host);
			System.out.println("IP  : " + inet.getHostAddress());
			assertEquals(ip, inet.getHostAddress());
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	// @Test
	public void testDynDnsUpdateAutoIp() throws InterruptedException
	{
		Random rnd = new Random();

		Scanner sc = new Scanner(System.in);
		String login = sc.nextLine();
		String password = sc.nextLine();
		String host = sc.nextLine();

		DynDnsUpdater updater = new DynDnsUpdater("", login, password);
		try
		{
			updater.update(host);
		}
		catch (JddUpdateException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	// @Test
	public void testGetIp() throws InterruptedException
	{
		Random rnd = new Random();

		Scanner sc = new Scanner(System.in);
		String host = sc.nextLine();
		InetAddress inet;
		try
		{
			inet = InetAddress.getByName(host);
			System.out.println("IP  : " + inet.getHostAddress());
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
