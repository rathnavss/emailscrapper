package com.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleSearch {

    private static Pattern patternDomainName;
    private Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN
            = "(https||https)://([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
    static {
        patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
    }

    private HashSet<String> searchLinks = new HashSet<>();
    private List<List<String>> emails = new ArrayList<>();


    public static void main(String[] args) {

//            Document doc = Jsoup
//                    .connect("https://www.google.com/search?q=mario")
//	                .userAgent("Mozilla/5.0")
//                    .timeout(5000).get();
//
//            Elements elements = doc.select("span.st");
//            for (Element e : elements) {
//                System.out.println("<p>Text : " + e.text()+"</p>");
//                System.out.println(extractEmail(e.text()));
//            }

            GoogleSearch search = new GoogleSearch();
            Set<String> result = search.getDataFromGoogle("mogo moringa");
            for(String temp : result){
                System.out.println(temp);
            }
            search.getEmail();

    }

    public static String extractEmail(String str) {
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(str);
        while (m.find()) {
            return m.group();
        }
        return null;
    }

    private Set<String> getDataFromGoogle(String query) {

        String request = "https://www.google.com/search?q=" + query + "&num=1";
        System.out.println("Sending request..." + request);

        try {

            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request)
                    .userAgent(
                            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(5000).get();

            // get all links
            Elements links = doc.select("a[href]");
            for (Element link : links) {

                String temp = link.attr("href");
                if(temp.startsWith("/url?q=")){
                    //use regex to get domain name
                    searchLinks.add(getDomainName(temp));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchLinks;
    }

    public String getDomainName(String url){

        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;

    }

    public void getEmail() {
        searchLinks.forEach(x -> {
            Document document;
            try {
                    document = Jsoup.connect(x).get();
                    String wholeHtml = document.body().html();
                    System.out.println(extractEmail(wholeHtml));

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

}
