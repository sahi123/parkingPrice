package parkingprice


import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

class ParkingPriceService {

    //Method which return the parking charges based on the entry and the exit date time.
    Integer calculatePrice(LocalDateTime entryLocalTime, LocalDateTime exitLocalTime) {
        Boolean isEntryDayWeekend = isWeekend(entryLocalTime);
        Boolean isExitDayWeekend = isWeekend(exitLocalTime);
        Integer parkingHour = Duration.between(entryLocalTime, exitLocalTime).toHours();
        if (parkingHour > 24)
            return 0
        if (isEntryDayWeekend == isExitDayWeekend) {
            return isEntryDayWeekend ? weekendPrice(parkingHour) : weekdayPrice(parkingHour)
        } else {
            Integer parkingHourInDay = hourDiffWithEntryTime(entryLocalTime)
            Integer parkingHourInOtherDay = hourDiffWithExitTime(exitLocalTime)
            Integer parkingHourInDayCharges = isEntryDayWeekend ? weekendPrice(parkingHourInDay) : weekdayPrice(parkingHourInDay)
            Long parkingHourInOtherDayCharges = isExitDayWeekend ? weekendPrice(parkingHourInOtherDay) : weekdayPrice(parkingHourInOtherDay)
            return parkingHourInDayCharges + parkingHourInOtherDayCharges
        }
    }

    //Method to calculate the weekday parking charges.
    Integer weekdayPrice(Integer parkingHour) {
        if (parkingHour) {
            if (parkingHour > 15 && parkingHour <= 24) {
                return parkingHour * 30;
            } else if (parkingHour > 10 && parkingHour <= 15) {
                return parkingHour * 22;
            } else if (parkingHour > 5 && parkingHour <= 10) {
                return parkingHour * 15;
            } else if (parkingHour > 2 && parkingHour <= 5) {
                return parkingHour * 10;
            } else if (parkingHour > 2) {
                return parkingHour * 7;
            }
        } else {
            return 0
        }
    }

    //Method to calculate the weekend parking charges.
    Integer weekendPrice(Integer parkingHour) {
        if (parkingHour) {
            if (parkingHour > 15 && parkingHour <= 24) {
                return parkingHour * 25;
            } else if (parkingHour > 10 && parkingHour <= 15) {
                return parkingHour * 18;
            } else if (parkingHour > 5 && parkingHour <= 10) {
                return parkingHour * 12;
            } else if (parkingHour > 2 && parkingHour <= 5) {
                return parkingHour * 8;
            } else if (parkingHour > 2) {
                return parkingHour * 5;
            }
        } else {
            return 0
        }
    }

    // To check whether the day is weekday or weekend.
    Boolean isWeekend(LocalDateTime date) {
        DayOfWeek day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));
        return day in [DayOfWeek.SATURDAY, DayOfWeek.SUNDAY];
    }

    //Utility method to convert Date String to LocalDateTime.
    LocalDateTime dateStringToLocalDateTime(String dateStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
        return localDateTime
    }
    //To calculate the number of parking hours in entry day.
    Integer hourDiffWithEntryTime(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();

        LocalDateTime lastDayTime = LocalDateTime.of(year, month, day, 23, 59, 59);
        Duration duration = Duration.between(dateTime, lastDayTime);
        return duration.toHours();
    }

    //To calculate the parking hours in exit day.
    Integer hourDiffWithExitTime(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();

        LocalDateTime startDayTime = LocalDateTime.of(year, month, day, 0, 0, 1);
        Duration duration = Duration.between(startDayTime, dateTime);
        return duration.toHours();
    }

    List<Map> parkingByVehicle(VehicleDao vehicleDao) {
        List<VehicleParkingDao> vehicleParkingDaoList = VehicleParkingDao.findAllByVehicle(vehicleDao, [sort: "entryTime", order: "asc"])
        List<Map> parkingDoneByVehicle = vehicleParkingDaoList.collect {
            [id: it.id, entryTime: it.entryTime, exitTime: it.exitTime, parkingPrice: it.parkingPrice]
        }
        return parkingDoneByVehicle
    }

    List<Map> parkingVehiclesList() {
        List<VehicleParkingDao> vehicleParkingDaoList = VehicleParkingDao.list([sort: "vehicleId", order: "asc"])
        List<Map> parkingVehiclesList = vehicleParkingDaoList.collect {
            [id: it.id, entryTime: it.entryTime, exitTime: it.exitTime, parkingPrice: it.parkingPrice]
        }
        return parkingVehiclesList
    }

}
