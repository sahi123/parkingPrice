package parkingprice

class VehicleDao {
    Long id
    String name
    String type

    static constraints = {
        type nullable: true
        name nullable: false
    }
}
