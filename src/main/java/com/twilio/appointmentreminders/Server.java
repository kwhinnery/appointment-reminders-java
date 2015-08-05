package com.twilio.appointmentreminders;

import com.twilio.appointmentreminders.controllers.AppointmentController;
import com.twilio.appointmentreminders.models.AppointmentService;
import com.twilio.appointmentreminders.util.AppSetup;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import spark.template.mustache.MustacheTemplateEngine;

import javax.persistence.EntityManagerFactory;

import static spark.Spark.*;
import spark.*;

public class Server {

  public static void main(String[] args) {
    AppSetup appSetup = new AppSetup();

    port(appSetup.getPortNumber());

    EntityManagerFactory factory = appSetup.getEntityManagerFactory();
    AppointmentService service = new AppointmentService(factory.createEntityManager());

    Spark.staticFileLocation("/public");

    Scheduler scheduler = null;
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();

      scheduler.start();

    } catch (SchedulerException se) {
      System.out.println("Unable to start scheduler service");
    }

    AppointmentController controller = new AppointmentController(service, scheduler);

    get("/", controller.index, new MustacheTemplateEngine());

    get("/new", controller.renderCreatePage, new MustacheTemplateEngine());

    post("/create", controller.create, new MustacheTemplateEngine());

    post("/delete", controller.delete);
  }
}
