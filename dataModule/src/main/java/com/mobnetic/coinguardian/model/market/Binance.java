package com.mobnetic.coinguardian.model.market;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mobnetic.coinguardian.model.CheckerInfo;
import com.mobnetic.coinguardian.model.CurrencyPairInfo;
import com.mobnetic.coinguardian.model.Market;
import com.mobnetic.coinguardian.model.Ticker;

public class Binance extends Market {

	private final static String NAME = "Binance";
	private final static String TTS_NAME = NAME;
	private final static String URL = "https://api.binance.com/api/v1/ticker/24hr?symbol=%1$s%2$s";
	private final static String URL_CURRENCY_PAIRS = "https://api.binance.com/api/v1/ticker/allPrices";
	private final static String[] BASE_CURRENCIES = {"BNB", "BTC", "ETH", "USDT"};

	public Binance() {
		super(NAME, TTS_NAME, null);
	}

	@Override
	public String getUrl(int requestId, CheckerInfo checkerInfo) {
		return String.format(URL, checkerInfo.getCurrencyBase(), checkerInfo.getCurrencyCounter());
	}

	@Override
	protected void parseTickerFromJsonObject(int requestId, JSONObject jsonObject, Ticker ticker, CheckerInfo checkerInfo) throws Exception {
		ticker.bid = jsonObject.getDouble("bidPrice");
		ticker.ask = jsonObject.getDouble("askPrice");
		ticker.vol = jsonObject.getDouble("volume");
		ticker.high = jsonObject.getDouble("highPrice");
		ticker.low = jsonObject.getDouble("lowPrice");
		ticker.last = jsonObject.getDouble("lastPrice");
	}

	// ====================
	// Get currency pairs
	// ====================
	@Override
	public String getCurrencyPairsUrl(int requestId) {
		return URL_CURRENCY_PAIRS;
	}

	@Override
	protected void parseCurrencyPairs(int requestId, String responseString, List<CurrencyPairInfo> pairs) throws Exception {
		final JSONArray resultJsonArray = new JSONArray(responseString);

		for(int i=0; i<resultJsonArray.length(); ++i) {
			final JSONObject marketJsonObject = resultJsonArray.getJSONObject(i);
			final String symbol = marketJsonObject.getString("symbol");

			for (String base : BASE_CURRENCIES) {
				if (symbol.endsWith(base)) {
					pairs.add(new CurrencyPairInfo(
							symbol.substring(0, symbol.lastIndexOf(base)),
							base,
							null));
				}
			}
		}
	}
}
