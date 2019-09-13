package com.nkvoronov.tvprogram.tasks;

import java.util.Date;
import org.jsoup.Jsoup;
import android.util.Log;
import java.util.Calendar;
import android.os.AsyncTask;
import org.jsoup.nodes.Element;
import java.text.ParseException;
import com.nkvoronov.tvprogram.R;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvschedule.TVSchedule;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.tvschedule.TVSchedulesList;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategory;
import static com.nkvoronov.tvprogram.common.HttpContent.HOST;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleDescription;
import static com.nkvoronov.tvprogram.common.MainDataSource.TAG;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategoriesList;
import static com.nkvoronov.tvprogram.common.StringUtils.parseString;

public class UpdateSchedulesTask extends AsyncTask<Integer,String,Void> {
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";

    public static final String STR_SEPDIRECTORS  = "Режиссер(ы):";
    public static final String STR_SEPACTORS  = "Актеры:";
    public static final String STR_SEPVENDORS  = "Ведущие:";
    public static final String STR_SEPBR  = "<br>";
    public static final String STR_SEPDIV  = "<div>";

    private int index;
    private int total;
    private int counter;
    private String[] progress;
    private TVChannelsList mChannels;
    private TVSchedulesList mSchedules;
    private OnTaskListeners mListeners;
    private MainDataSource mDataSource;

    public UpdateSchedulesTask(MainDataSource dataSource) {
        mDataSource = dataSource;
        mChannels = null;
        index = 0;
    }

    public interface OnTaskListeners {
        public void onStart();
        public void onUpdate(String[] progress);
        public void onStop();
    }

    public void setListeners(OnTaskListeners listeners) {
        mListeners = listeners;
    }

    @Override
    protected void onPreExecute() {
        mListeners.onStart();
    }

    @Override
    protected Void doInBackground(Integer... values) {
        int type_channels = values[0];
        index = 0;
        progress = new String[] {"", "", ""};
        mSchedules = mDataSource.getSchedules(0, String.valueOf(-1), null);
        mSchedules.clear();
        Date date = new Date();
        if (type_channels == -1) {
            mChannels = mDataSource.getChannels(true, 0);
            total = mChannels.size() * mDataSource.getCoutDays();
            for (TVChannel channel : mChannels.getData()) {
                progress[0] = channel.getName();
                mSchedules.preUpdateSchedules(channel.getIndex());
                getContentForChannel(channel.getIndex(), date);
            }
        } else {
            total = 1 * mDataSource.getCoutDays();
            progress[0] = "-1";
            mSchedules.preUpdateSchedules(type_channels);
            getContentForChannel(type_channels, date);
        }
        mSchedules.setScheduleEnding();
        progress[0] = "0";
        progress[1] = "";
        progress[2] = String.valueOf(counter);
        publishProgress(progress);
        mSchedules.saveToDB();
        return null;
    }

    public void getContentForChannel(int channel, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Calendar calendar_last = Calendar.getInstance();
        calendar.setTime(date);
        calendar_last.setTime(date);
        calendar_last.add(Calendar.DATE, mDataSource.getCoutDays());
        while (!calendar.getTime().equals(calendar_last.getTime())) {
            progress[1] = dateFormat.format(calendar.getTime());
            getContentForDay(channel, calendar.getTime());
            counter = (int) (((index+1) / (float) total) * 100);
            progress[2] = String.valueOf(counter);
            publishProgress(progress);
            if(isCancelled()){
                break;
            }
            calendar.add(Calendar.DATE, 1);
            index++;
        }
    }

    public void getContentForDay(int channel, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String otime = "00:00";
        String direction = String.format(STR_SCHEDULECHANNEL, channel, dateFormat.format(date));
        org.jsoup.nodes.Document doc = new HttpContent(HOST + direction).getDocument();
        Elements elements = doc.select(STR_ELMDOCSELECT);
        for (org.jsoup.nodes.Element element : elements){
            //Time
            String string_time = element.html().trim();
            Date startDate = null;
            Date endDate = null;
            if (Integer.parseInt(string_time.split(":")[0]) < Integer.parseInt(otime.split(":")[0])) {
                calendar.add(Calendar.DATE, 1);
            }
            otime = string_time;
            try {
                startDate = dateTimeFormat.parse(dateFormat.format(calendar.getTime()) + " " + string_time + ":00");
                endDate = dateTimeFormat.parse(dateFormat.format(calendar.getTime()) + " 23:59:59");
            } catch (ParseException e) {
                e.fillInStackTrace();
                Log.d(TAG, e.getMessage());
            }
            //Title
            String string_title = "";
            String string_full_description_url = "";
            Element element_title = element.nextElementSibling();
            try {
                if (element_title != null) {
                    Elements elements_title = element_title.select(STR_ELMDOCTITLE);
                    if (elements_title != null) {
                        string_full_description_url = elements_title.select("a").attr("href");
                        string_title = elements_title.text();
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
                Log.d(TAG, e.getMessage());
            }
            //Description
            String string_description = "";
            String string_description_head = "";
            Element element_description = element_title.nextElementSibling();
            try {
                if (element_description != null) {
                    Elements elements_description = element_description.select(STR_ELMDOCDESC);
                    if (elements_description != null) {
                        string_description = elements_description.html();
                        string_description_head = elements_description.select("b").text();
                        string_description = Jsoup.parse(string_description.replaceAll("<br>", ";")
                                .replace(string_description_head, ""))
                                .text()
                                .replaceAll(";", "<br>");
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
                Log.d(TAG, e.getMessage());
            }
            TVSchedule schedule = new TVSchedule(-1, channel, startDate, endDate, string_title);
            schedule.setFavorites(false);
            setCategory(schedule);
            setDescription(schedule, string_description, string_description_head, string_full_description_url);
            mSchedules.add(schedule);
        }
    }

    private Boolean titleContainsDictWorlds(String title, String dictionary) {
        Boolean res = false;
        String[] list = dictionary.split(",");
        for (String str:list) {
            res = res || title.contains(str);
        }
        return res;
    }

    private void setCategory(TVSchedule schedule) {
        TVScheduleCategoriesList categories = mDataSource.getCategories();
        for (TVScheduleCategory category : categories.getData()) {
            if (titleContainsDictWorlds(schedule.getTitle().toLowerCase(), category.getDictionary())) {
                schedule.setCategory(category.getId());
                break;
            }
        }
    }

    private void setDescription(TVSchedule schedule, String description, String head_description, String url_description) {
        if (description.length() > 0 && !description.equals("")) {
            if (schedule.getDescription() == null) {
                schedule.setDescription(new TVScheduleDescription(""));
                schedule.getDescription().setDataSource(mDataSource);
            }

            if (head_description.length() > 0 && !head_description.equals("")) {
                String[] list = head_description.split(",");
                schedule.getDescription().setCountry(list[0].trim());
                schedule.getDescription().setYear(list[1].trim());
                schedule.getDescription().setGenres(list[2].trim().replace(" / ", ", "));
            }

            String[] list_desc = description.replaceFirst("<br>", "").split(" <br> <br>");
            if (list_desc[0].length() > 0) {
                schedule.getDescription().setActors(list_desc[0]);
            }
            if (list_desc[1].length() > 0) {
                schedule.getDescription().setDescription(list_desc[1] + "<br><br>");
            }
        }
        if (url_description.length() > 0 && !url_description.equals("")) {
            if (schedule.getDescription() == null) {
                schedule.setDescription(new TVScheduleDescription(""));
                schedule.getDescription().setDataSource(mDataSource);
            }
            String direction = HOST + url_description;
            //Parse url
            String[] list_url = url_description.replace(".html", "").split("_");
            String type = list_url[0].trim();
            schedule.getDescription().setType(type);
            String catalog = list_url[1].trim();
            schedule.getDescription().setIdCatalog(Integer.parseInt(catalog));
            if (schedule.getCategory() == 0) {
                if (type.equals("film")) {
                    schedule.setCategory(1);
                }
                if (type.equals("series")) {
                    schedule.setCategory(2);
                }
                if (type.equals("show")) {
                    schedule.setCategory(7);
                }
            }

            String txt_det = mDataSource.getContext().getString(R.string.txt_details);

            if (mDataSource.isFullDesc()) {
                String type_desc = schedule.getDescription().getType();
                int catalog_desc = schedule.getDescription().getIdCatalog();
                TVSchedule copy_schedule = mSchedules.getScheduleForType(type_desc, catalog_desc);
                if (copy_schedule != null) {
                    schedule.copyFullDesc(copy_schedule);
                } else {
                    org.jsoup.nodes.Document doc = new HttpContent(direction).getDocument();
                    String string_showname = "";
                    String string_title = "";
                    String string_genre = "";
                    try {
                        Elements elements_showname = doc.select("td.showname");
                        if (elements_showname != null) {
                            string_showname = elements_showname.html();
                            //Title
                            if (string_showname.indexOf("<strong>") != -1) {
                                string_showname = parseString(string_showname, "</h2>", "<strong>")
                                        .replace("</h2>", "")
                                        .trim();
                            } else {
                                string_showname = parseString(string_showname, "</h2>", "<!--")
                                        .replace("</h2>", "")
                                        .replace("&nbsp;", "")
                                        .trim();
                            }
                            if (string_showname.length() > 0) {
                                if (string_showname.endsWith(",")) {
                                    string_showname = string_showname.substring(0, string_showname.length() - 1).trim();
                                }
                                if (string_showname.endsWith("-")) {
                                    string_showname = string_showname.substring(0, string_showname.length() - 1);
                                }
                                string_showname = string_showname.replaceFirst("<br>", ";");
                                //TitleOrg
                                string_title = string_showname.split(";")[0].trim();
                                if (string_title.length() > 0) {
                                    schedule.getDescription().setTitle(string_title);
                                }
                                string_showname = string_showname.split(";")[1]
                                        .replaceAll("<br>", "")
                                        .trim();
                                String[] list_data = string_showname.split(",");
                                if (list_data.length == 1) {
                                    //Year
                                    schedule.getDescription().setYear(list_data[0].trim());
                                } else {
                                    //Country
                                    schedule.getDescription().setCountry(list_data[0].trim());
                                    //Year
                                    schedule.getDescription().setYear(list_data[1].trim());
                                }
                            }
                            //Genres
                            string_genre = elements_showname.select("strong").text().replace(" / ", ", ");
                            schedule.getDescription().setGenres(string_genre);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                    String string_showmain = "";
                    String string_directors = "";
                    String string_actors = "";
                    String string_vendors = "";
                    try {
                        Elements elements_showmain = doc.select("td.showmain");
                        if (elements_showmain != null) {
                            string_showmain = elements_showmain.html();
                            //Directors
                            string_directors = parseString(string_showmain, STR_SEPDIRECTORS, STR_SEPBR);
                            if (string_directors.length() > 0) {
                                string_directors = Jsoup.parse(string_directors)
                                        .text()
                                        .replace(STR_SEPDIRECTORS, "")
                                        .trim();
                                schedule.getDescription().setDirectors(string_directors);
                            }
                            //Actors
                            string_actors = parseString(string_showmain, STR_SEPACTORS, STR_SEPDIV);
                            if (string_actors.length() > 0) {
                                string_actors = Jsoup.parse(string_actors)
                                        .text()
                                        .replace(STR_SEPACTORS, "")
                                        .trim();
                                schedule.getDescription().setActors(string_actors);
                            }
                            //Vendors
                            string_vendors = parseString(string_showmain, STR_SEPVENDORS, STR_SEPDIV);
                            if (string_vendors.length() > 0) {
                                string_vendors = Jsoup.parse(string_vendors)
                                        .text()
                                        .replace(STR_SEPVENDORS, "")
                                        .trim();
                                if (schedule.getDescription().getActors().length() > 0) {
                                    schedule.getDescription().setActors(schedule.getDescription().getActors() + ", " + string_vendors);
                                } else {
                                    schedule.getDescription().setActors(string_vendors);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                    //Image
                    String string_img = "";
                    try {
                        string_img = doc.select("img.mb_img").attr("src");
                        if (string_img.length() > 0) {
                            string_img = HOST + string_img.replaceFirst("/", "");
                            schedule.getDescription().setImage(string_img);
                            //Download image
                            schedule.getDescription().saveImageToFile();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                    //Full Description
                    String string_description = "";
                    try {
                        Elements elements_big = doc.select("span.big");
                        string_description = elements_big.html();
                        string_description = Jsoup.parse(string_description.replaceAll("<br>", ";"))
                                .text()
                                .replaceAll(";", "<br>")
                                .trim();
                        if (string_description.length() > 0) {
                            schedule.getDescription().setDescription(string_description + "<br><br>");
                        }
                        schedule.getDescription().setDescription(schedule.getDescription().getDescription() + "<a href=\"" + direction + "\">" + txt_det + "</a>");

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                    //Rating
                    String string_rating = "";
                    try {
                        Elements elements_name = doc.select("span.name");
                        string_rating = elements_name.get(0).text().split(":")[1].trim();
                        schedule.getDescription().setRating(string_rating);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                schedule.getDescription().setDescription(schedule.getDescription().getDescription() + "<a href=\"" + direction + "\">" + txt_det + "</a>");
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mListeners.onUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListeners.onStop();
    }

}
