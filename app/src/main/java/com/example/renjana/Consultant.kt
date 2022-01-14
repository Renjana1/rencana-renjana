package com.example.renjana

class Consultant {
    var id: String?=null
    var name: String? = null
    var title: String? = null
    var description: String? = null
    var image: String? = null

    constructor(id: String?, name: String?, title: String?, description: String?, image: String?) {
        this.id = id
        this.name = name
        this.title = title
        this.description = description
        this.image = image
    }
}