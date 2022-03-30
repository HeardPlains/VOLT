package com.volt.voltdata.appdata

class CurrentForeman(
    first_name: String,
    last_name: String,
    foreman_id: Int,
    current_location: String
) {
    var firstName: String = first_name
    var foremanId: Int = foreman_id
    var lastName: String = last_name
    var currentLocation: String = current_location
    var fullName: String = "$first_name $last_name"

}