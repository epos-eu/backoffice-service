const fs = require('fs');


types = ["DataProduct", "Distribution", "WebService", "Operation", "ContactPoint", "Person", "Organization"]

start = "openapi: 3.0.3\n" +
    "info:\n" +
    "  title: EPOS Backoffice RESTful APIs\n" +
    "  description: This is the Backoffice RESTful APIs Swagger page.\n" +
    "  contact:\n" +
    "    email: apis@lists.epos-ip.org\n" +
    "  license:\n" +
    "    name: MIT License\n" +
    "    url: https://epos-ci.brgm.fr/epos/WebApi/raw/master/LICENSE\n" +
    "  version: 2.0.0\n" +
    "servers:\n" +
    "  - url: http://localhost:8080/api/backoffice/v1\n" +
    "    description: Inferred Url\n" +
    "paths:\n" +
    "  /user:\n" +
    "    post:\n" +
    "      tags:\n" +
    "        - V3 - User\n" +
    "      summary: \"Create User instance\"\n" +
    "      description: \"\"\n" +
    "      operationId: \"userpost\"\n" +
    "      requestBody:\n" +
    "        content:\n" +
    "          'application/json':\n" +
    "            schema:\n" +
    "              $ref: '#/components/schemas/user'\n" +
    "      responses:\n" +
    "        '200':\n" +
    "          description: ok.\n" +
    "        '201':\n" +
    "          description: Created.\n" +
    "        '204':\n" +
    "          description: No content.\n" +
    "        '400':\n" +
    "          description: Bad request.\n" +
    "        '401':\n" +
    "          description: Token is missing or invalid\n" +
    "        '403':\n" +
    "          description: Forbidden\n" +
    "        '404':\n" +
    "          description: Not Found\n" +
    "        '405':\n" +
    "          description: 'Invalid input'\n" +
    "        '500':\n" +
    "          description: 'Internal Server Error'\n" +
    "        '501':\n" +
    "          description: 'Not Implemented'\n" +
    "      security:\n" +
    "        - aai: []\n" +
    "    put:\n" +
    "      tags:\n" +
    "        - V3 - User\n" +
    "      summary: \"Update Data Product\"\n" +
    "      description: \"\"\n" +
    "      operationId: \"userputstate\"\n" +
    "      requestBody:\n" +
    "        content:\n" +
    "          'application/json':\n" +
    "            schema:\n" +
    "              $ref: '#/components/schemas/user'\n" +
    "      responses:\n" +
    "        '200':\n" +
    "          description: ok.\n" +
    "        '204':\n" +
    "          description: No content.\n" +
    "        '301':\n" +
    "          description: Moved Permanently.\n" +
    "        '400':\n" +
    "          description: Bad request.\n" +
    "        '401':\n" +
    "          description: Token is missing or invalid\n" +
    "        '403':\n" +
    "          description: Forbidden\n" +
    "        '404':\n" +
    "          description: Not Found\n" +
    "        '405':\n" +
    "          description: 'Invalid input'\n" +
    "        '500':\n" +
    "          description: 'Internal Server Error'\n" +
    "        '501':\n" +
    "          description: 'Not Implemented'\n" +
    "      security:\n" +
    "        - aai: []\n" +
    "  /user/{instance_id}:\n" +
    "    get:\n" +
    "      tags:\n" +
    "        - V3 - User\n" +
    "      summary: \"Retrive Data Product\"\n" +
    "      description: \"\"\n" +
    "      operationId: \"userget\"\n" +
    "      parameters:\n" +
    "        - in: \"query\"\n" +
    "          name: \"userId\"\n" +
    "          description: \"eduIdentifier\"\n" +
    "          required: false\n" +
    "          schema:\n" +
    "            type: string\n" +
    "        - in: \"path\"\n" +
    "          name: \"instance_id\"\n" +
    "          description: \"Id of the object\"\n" +
    "          required: true\n" +
    "          schema:\n" +
    "            type: string\n" +
    "        - in: \"query\"\n" +
    "          name: \"available_section\"\n" +
    "          description: \"If it is flagged as true, returns the user (or the list of users) populated with the set of section accessible that user\"\n" +
    "          required: false\n" +
    "          schema:\n" +
    "            type: boolean\n" +
    "      responses:\n" +
    "        '200':\n" +
    "          description: ok.\n" +
    "          content:\n" +
    "            application/json:\n" +
    "              schema:\n" +
    "                $ref: '#/components/schemas/userList'\n" +
    "        '204':\n" +
    "          description: No content.\n" +
    "        '301':\n" +
    "          description: Moved Permanently.\n" +
    "        '400':\n" +
    "          description: Bad request.\n" +
    "        '401':\n" +
    "          description: Token is missing or invalid\n" +
    "        '403':\n" +
    "          description: Forbidden\n" +
    "        '404':\n" +
    "          description: Not Found\n" +
    "        '405':\n" +
    "          description: 'Invalid input'\n" +
    "        '500':\n" +
    "          description: 'Internal Server Error'\n" +
    "        '501':\n" +
    "          description: 'Not Implemented'\n" +
    "      security:\n" +
    "        - aai: []\n" +
    "    delete:\n" +
    "      tags:\n" +
    "        - V3 - User\n" +
    "      summary: \"Delete Data Product\"\n" +
    "      description: \"\"\n" +
    "      operationId: \"userdelete\"\n" +
    "      parameters:\n" +
    "        - in: \"path\"\n" +
    "          name: \"instance_id\"\n" +
    "          description: \"instanceId of the object\"\n" +
    "          required: true\n" +
    "          schema:\n" +
    "            type: string\n" +
    "      responses:\n" +
    "        '200':\n" +
    "          description: ok.\n" +
    "          content:\n" +
    "            application/json:\n" +
    "              schema:\n" +
    "                $ref: '#/components/schemas/user'\n" +
    "        '204':\n" +
    "          description: No content.\n" +
    "        '301':\n" +
    "          description: Moved Permanently.\n" +
    "        '400':\n" +
    "          description: Bad request.\n" +
    "        '401':\n" +
    "          description: Token is missing or invalid\n" +
    "        '403':\n" +
    "          description: Forbidden\n" +
    "        '404':\n" +
    "          description: Not Found\n" +
    "        '405':\n" +
    "          description: 'Invalid input'\n" +
    "        '500':\n" +
    "          description: 'Internal Server Error'\n" +
    "        '501':\n" +
    "          description: 'Not Implemented'\n" +
    "      security:\n" +
    "        - aai: []"

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

types.forEach(type => {
    const down_type = type.toLowerCase();
    let template = "\n\n  /"+ down_type+":\n" +
        "    post:\n" +
        "      tags:\n" +
        "        - V3 - "+ type +"\n" +
        "      summary: \"Create "+ type +" instance\"\n" +
        "      description: \"\"\n" +
        "      operationId: \""+ down_type+"post\"\n" +
        "      requestBody:\n" +
        "        content:\n" +
        "          'application/json':\n" +
        "            schema:\n" +
        "              $ref: '#/components/schemas/"+ down_type+"'\n" +
        "      responses:\n" +
        "        '201':\n" +
        "          description: Created.\n" +
        "        '204':\n" +
        "          description: No content.\n" +
        "        '400':\n" +
        "          description: Bad request.\n" +
        "        '401':\n" +
        "          description: Token is missing or invalid\n" +
        "        '403':\n" +
        "          description: Forbidden\n" +
        "        '404':\n" +
        "          description: Not Found\n" +
        "        '405':\n" +
        "          description: 'Invalid input'\n" +
        "        '500':\n" +
        "          description: 'Internal Server Error'\n" +
        "        '501':\n" +
        "          description: 'Not Implemented'\n" +
        "      security:\n" +
        "        - aai: []\n" +
        "    put:\n" +
        "      tags:\n" +
        "        - V3 - "+ type +"\n" +
        "      summary: \"Update "+ type +"\"\n" +
        "      description: \"\"\n" +
        "      operationId: \""+ down_type+"putstate\"\n" +
        "      requestBody:\n" +
        "        content:\n" +
        "          'application/json':\n" +
        "            schema:\n" +
        "              $ref: '#/components/schemas/"+ down_type+"'\n" +
        "      responses:\n" +
        "        '200':\n" +
        "          description: ok.\n" +
        "        '204':\n" +
        "          description: No content.\n" +
        "        '301':\n" +
        "          description: Moved Permanently.\n" +
        "        '400':\n" +
        "          description: Bad request.\n" +
        "        '401':\n" +
        "          description: Token is missing or invalid\n" +
        "        '403':\n" +
        "          description: Forbidden\n" +
        "        '404':\n" +
        "          description: Not Found\n" +
        "        '405':\n" +
        "          description: 'Invalid input'\n" +
        "        '500':\n" +
        "          description: 'Internal Server Error'\n" +
        "        '501':\n" +
        "          description: 'Not Implemented'\n" +
        "      security:\n" +
        "        - aai: []\n" +
        "  /"+ down_type+"/{instance_id}:\n" +
        "    get:\n" +
        "      tags:\n" +
        "        - V3 - "+ type +"\n" +
        "      summary: \"Retrive "+ type +"\"\n" +
        "      description: \"\"\n" +
        "      operationId: \""+ down_type+"get\"\n" +
        "      parameters:\n" +
        "        - in: \"path\"\n" +
        "          name: \"instance_id\"\n" +
        "          description: \"Instance Id of the object\"\n" +
        "          required: true\n" +
        "          schema:\n" +
        "            type: string\n" +
        "      responses:\n" +
        "        '200':\n" +
        "          description: ok.\n" +
        "          content:\n" +
        "            application/json:\n" +
        "              schema:\n" +
        "                $ref: '#/components/schemas/"+ down_type+"'\n" +
        "        '204':\n" +
        "          description: No content.\n" +
        "        '301':\n" +
        "          description: Moved Permanently.\n" +
        "        '400':\n" +
        "          description: Bad request.\n" +
        "        '401':\n" +
        "          description: Token is missing or invalid\n" +
        "        '403':\n" +
        "          description: Forbidden\n" +
        "        '404':\n" +
        "          description: Not Found\n" +
        "        '405':\n" +
        "          description: 'Invalid input'\n" +
        "        '500':\n" +
        "          description: 'Internal Server Error'\n" +
        "        '501':\n" +
        "          description: 'Not Implemented'\n" +
        "      security:\n" +
        "        - aai: []\n" +
        "    delete:\n" +
        "      tags:\n" +
        "        - V3 - "+ type +"\n" +
        "      summary: \"Delete "+ type +"\"\n" +
        "      description: \"\"\n" +
        "      operationId: \""+ down_type+"delete\"\n" +
        "      parameters:\n" +
        "        - in: \"path\"\n" +
        "          name: \"instance_id\"\n" +
        "          description: \"Id of the object\"\n" +
        "          required: true\n" +
        "          schema:\n" +
        "            type: string\n" +
        "      responses:\n" +
        "        '200':\n" +
        "          description: ok.\n" +
        "          content:\n" +
        "            application/json:\n" +
        "              schema:\n" +
        "                $ref: '#/components/schemas/"+ down_type+"'\n" +
        "        '204':\n" +
        "          description: No content.\n" +
        "        '301':\n" +
        "          description: Moved Permanently.\n" +
        "        '400':\n" +
        "          description: Bad request.\n" +
        "        '401':\n" +
        "          description: Token is missing or invalid\n" +
        "        '403':\n" +
        "          description: Forbidden\n" +
        "        '404':\n" +
        "          description: Not Found\n" +
        "        '405':\n" +
        "          description: 'Invalid input'\n" +
        "        '500':\n" +
        "          description: 'Internal Server Error'\n" +
        "        '501':\n" +
        "          description: 'Not Implemented'\n" +
        "      security:\n" +
        "        - aai: []";
    start = start + template
})

//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------

start = start + "\n" +
    "components:\n" +
    "  schemas:\n" +
    "    EntityType:\n" +
    "      enum:\n" +
    "        - DataProduct\n" +
    "        - Distribution\n" +
    "        - WebService\n" +
    "        - Operation\n" +
    "        - ContactPoint\n" +
    "        - Person\n" +
    "        - Organization\n" +
    "      type: string\n" +
    "    Status:\n" +
    "      enum:\n" +
    "        - Draft\n" +
    "        - Submitted\n" +
    "        - Discarded\n" +
    "        - Published\n" +
    "        - Archived\n" +
    "      type: string\n" +
    "    contactpoint:\n" +
    "      required:\n" +
    "        - UID\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: uid\n" +
    "          description: 'This property contains the main identifier for the Contact Point'\n" +
    "        email:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'email'\n" +
    "          description: 'This property refers to the Contact Point Email.'\n" +
    "        language:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'language'\n" +
    "          description: 'This property contains information about the language used by the ContactPoint. Please use one of the language codes from the IETF BCP 47 standard.'\n" +
    "        role:\n" +
    "          type: string\n" +
    "          example: 'role'\n" +
    "          description: 'This property refers to the Contact Point role.'\n" +
    "        person:\n" +
    "          type: string\n" +
    "          example: 'person UID'\n" +
    "          description: 'This property refers to the person who is the Contact Point.'\n" +
    "        telephone:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'telephone'\n" +
    "          description: 'This property refers to the Contact Point Telephone.'\n" +
    "    user:\n" +
    "      required:\n" +
    "        - UID\n" +
    "      type: object\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: uid\n" +
    "          description: 'This property contains the main identifier for the Data Product, e.g. the URI or other unique identifier in the context of the Catalogue.'\n" +
    "    userList:\n" +
    "      type: array\n" +
    "      items:\n" +
    "        $ref: '#/components/schemas/user'\n" +
    "    dataproduct:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - description\n" +
    "        - title\n" +
    "      type: object\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: uid\n" +
    "          description: 'This property contains the main identifier for the Data Product, e.g. the URI or other unique identifier in the context of the Catalogue.'\n" +
    "        accessRight:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'access right'\n" +
    "          description: 'This property refers to information that indicates whether the Data Product is open data, has access restrictions or is not public.'\n" +
    "        accrualPeriodicity:\n" +
    "          type: string\n" +
    "          example: 'accrual periodicity'\n" +
    "          description: 'This property refers to the frequency at which the Data Product is updated.'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Data Product. A Data Product may be associated with multiple categories.'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contactPoint UID'\n" +
    "          description: 'This property contains contact information (i.e. Role) that can be used for sending comments about the Data Product.'\n" +
    "        created:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the date on which the Data Product was created.'\n" +
    "        description:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'description'\n" +
    "          description: 'This property contains a free-text account of the Data Product. This property can be repeated for parallel language versions of the description.'\n" +
    "        distribution:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'distribution UID'\n" +
    "          description: 'This property links the Data Product to an available Distribution.'\n" +
    "        hasPart:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'dataproduct UID'\n" +
    "          description: 'This property refers to a related Data Product that is part of the described Data Product.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/identifier'\n" +
    "          description: 'This property refers to a secondary identifier of the Data Product, such as MAST/ADS, DataCite, DOI, EZID or W3ID.'\n" +
    "        isPartOf:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'dataproduct'\n" +
    "          description: 'This property refers to a related Data Product in which the described Data Product is physically or logically included.'\n" +
    "        issued:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the date of formal issuance (e.g., publication) of the Data Product.'\n" +
    "        keywords:\n" +
    "          type: string\n" +
    "          example: 'keyword1, keyword2'\n" +
    "          description: 'This property contains a keyword or tag describing the Data Product. Multiple entries in a keywords list are typically delimited by commas.'\n" +
    "        modified:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the most recent date on which the Data Product was changed or modified.'\n" +
    "        provenance:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'provenance'\n" +
    "          description: 'This property contains a statement about the lineage of a Data Product'\n" +
    "        publisher:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'organization'\n" +
    "          description: 'This property refers to an entity (organisation) responsible for making the Data Product available.'\n" +
    "        relation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'epos resource'\n" +
    "          description: 'It represents the link to another Epos resource.'\n" +
    "        spatialExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/location'\n" +
    "          description: 'This property refers to a geographic region that is covered by the Data Product.'\n" +
    "        temporalExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/periodOfTime'\n" +
    "          description: 'This property refers to a temporal period (i.e. startDate, endDate) that the Data Product covers.'\n" +
    "        title:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: title\n" +
    "          description: 'This property contains a name given to the Data Product. This property can be repeated for parallel language versions of the name.'\n" +
    "        type:\n" +
    "          type: string\n" +
    "          example: type\n" +
    "          description: 'This property refers to the type of the Data Product. A controlled vocabulary for the values has not been established.'\n" +
    "        versionInfo:\n" +
    "          type: string\n" +
    "          example: '1.0'\n" +
    "          description: 'This property contains a version number or other version designation of the Data Product.'\n" +
    "    equipment:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - description\n" +
    "        - name\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Equipment.'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Equipment. An Equipment may be associated with multiple categories.'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact Point'\n" +
    "          description: 'This property contains contact information (i.e. Role) that can be used for sending comments about the Equipment.'\n" +
    "        description:\n" +
    "          type: string\n" +
    "          example: 'description'\n" +
    "          description: 'This property contains a free-text description of the Equipment.'\n" +
    "        dynamicRange:\n" +
    "          type: string\n" +
    "          example: 'dynamicRange'\n" +
    "          description: 'This property contains the full-scale measuring ability, in nT (unit and value).'\n" +
    "        filter:\n" +
    "          type: string\n" +
    "          example: 'filter'\n" +
    "          description: 'This property describes the filter that the instrument uses to produce data.'\n" +
    "        isPartOf:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'isPartOf'\n" +
    "          description: 'This property refers to an Equipment or a Facility in which the described Equipment is included.'\n" +
    "        manufacturer:\n" +
    "          type: string\n" +
    "          example: 'manufacturer'\n" +
    "          description: 'This property refers to an Organisation responsible for manufacturing the Equipment.'\n" +
    "        name:\n" +
    "          type: string\n" +
    "          example: 'name'\n" +
    "          description: 'This property contains a name given to the Equipment.'\n" +
    "        pageURL:\n" +
    "          type: string\n" +
    "          example: 'pageURL'\n" +
    "          description: 'This property refers to a page or document about the Equipment'\n" +
    "        orientation:\n" +
    "          type: string\n" +
    "          example: 'orientation'\n" +
    "          description: 'This property describes how the instrument is oriented.'\n" +
    "        relation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'Data Product or Webservice or Service'\n" +
    "          description: 'It represents the link to another Epos resource. e.g., Dataset, WebService, Service'\n" +
    "        resolution:\n" +
    "          type: string\n" +
    "          example: 'resolution'\n" +
    "          description: 'This property contains the resolution in nT.'\n" +
    "        samplePeriod:\n" +
    "          type: string\n" +
    "          example: 'samplePeriod'\n" +
    "          description: 'This property contains the sample period in mS (unit and value)'\n" +
    "        serialNumber:\n" +
    "          type: string\n" +
    "          example: 'serialNumber'\n" +
    "          description: 'This property contains the serial number for the Equipment.'\n" +
    "        spatialExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/location'\n" +
    "          description: 'This property refers to a geographical location of the Equipment.'\n" +
    "        temporalExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/periodOfTime'\n" +
    "          description: 'This property refers to a temporal period (i.e. startDate, endDate) that the Equipment covers.'\n" +
    "        type:\n" +
    "          type: string\n" +
    "          example: 'type'\n" +
    "          description: 'This property refers to the type of the Equipment.'\n" +
    "    facility:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - Description\n" +
    "        - Title\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Facility.'\n" +
    "        address:\n" +
    "          $ref: '#/components/schemas/address'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Facility. A Facility may be associated with multiple categories.'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact Point'\n" +
    "          description: 'This property contains contact information (i.e. Role) that can be used for sending comments about the Facility'\n" +
    "        description:\n" +
    "          type: string\n" +
    "          example: 'description'\n" +
    "          description: 'This property contains a free-text description of the Facility.'\n" +
    "        isPartOf:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'facility'\n" +
    "          description: 'This property refers to a Facility in which the described Facility is included.'\n" +
    "        pageURL:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'pageURL'\n" +
    "          description: 'This property refers to a page or document about this Facility.'\n" +
    "        relation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'dataproduct or contactPoint or service'\n" +
    "          description: 'It represents the link to another Epos resource. e.g., Dataset, WebService, Service'\n" +
    "        spatialExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/location'\n" +
    "          description: 'This property refers to a geographical location of the Facility.'\n" +
    "        title:\n" +
    "          type: string\n" +
    "          example: 'title'\n" +
    "          description: 'This property contains a name given to the Facility.'\n" +
    "        type:\n" +
    "          type: string\n" +
    "          example: 'type'\n" +
    "          description: 'This property refers to the type of the Facility.'\n" +
    "    organization:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - identifier\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Organization.'\n" +
    "        address:\n" +
    "          $ref: '#/components/schemas/address'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact Point'\n" +
    "          description: 'This property refers to the Contact Point (i.e. Role) defined for the Organization'\n" +
    "        email:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'email'\n" +
    "          description: 'This property contains the email address of the Organization.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/identifier'\n" +
    "          description: 'This property contains an identifier for the Organization (e.g., PIC, ISNI, etc.).'\n" +
    "        legalName:\n" +
    "          type: string\n" +
    "          example: 'legalName'\n" +
    "          description: 'This property contains the official name of the Organization.'\n" +
    "        leiCode:\n" +
    "          type: string\n" +
    "          example: 'leiCode'\n" +
    "          description: 'This property contains the Organization identifier that uniquely identifies a legal entity as defined in ISO 17442.'\n" +
    "        logo:\n" +
    "          type: string\n" +
    "          example: 'logo'\n" +
    "          description: 'This property contains the URL of the Organization logo.'\n" +
    "        memberOf:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'organization'\n" +
    "          description: 'This property refers to an Organization to which this Organization belongs.'\n" +
    "        owns:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'equipment or facility'\n" +
    "          description: 'This property refers to a Facility or Equipment of which it is the owner.'\n" +
    "        telephone:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'telephone'\n" +
    "          description: 'This property contains the telephone number of the Organization.'\n" +
    "        URL:\n" +
    "          type: string\n" +
    "          example: 'URL'\n" +
    "          description: 'This property contains the URL of the Organization website.'\n" +
    "    person:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - identifier\n" +
    "        - familyName\n" +
    "        - givenName\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Person.'\n" +
    "        address:\n" +
    "          $ref: '#/components/schemas/address'\n" +
    "        affiliation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'organization'\n" +
    "          description: 'This property refers to the organization to which the person is affiliated.'\n" +
    "        CVURL:\n" +
    "          type: string\n" +
    "          example: 'https://mycv.com/mario.rossi'\n" +
    "          description: 'This property refers to the URL of the Person curriculum vitae.'\n" +
    "        email:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'mario.rossi@gmail.com'\n" +
    "          description: 'This property contains the email address of the Person.'\n" +
    "        familyName:\n" +
    "          type: string\n" +
    "          example: 'Rossi'\n" +
    "          description: 'This property contains the last name of the Person.'\n" +
    "        givenName:\n" +
    "          type: string\n" +
    "          example: 'Mario'\n" +
    "          description: 'This property contains the first name of the Person.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/identifier'\n" +
    "          description: 'This property contains an identifier for the Person (e.g., ORCID, ScopusID, etc.).'\n" +
    "        qualification:\n" +
    "          type: string\n" +
    "          example: 'boss'\n" +
    "          description: 'This property contains the specific qualifications of the Person.'\n" +
    "        telephone:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: '+39 334 86 06 438'\n" +
    "          description: 'This property contains the telephone number of the Person.'\n" +
    "    publication:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - identifier\n" +
    "        - name\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Publication.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/identifier'\n" +
    "          description: 'This property contains an identifier for the Publication.'\n" +
    "        name:\n" +
    "          type: string\n" +
    "          example: 'name'\n" +
    "          description: 'This property contains the name of the Publication.'\n" +
    "        published:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the date of first Publication.'\n" +
    "        publisher:\n" +
    "          type: string\n" +
    "          example: 'Publisher'\n" +
    "          description: 'This property refers to an organization or person or agent which is the publisher of the Publication.'\n" +
    "        abstract:\n" +
    "          type: string\n" +
    "          example: 'Mario'\n" +
    "          description: 'This property contains the abstract of the Publication.'\n" +
    "        author:\n" +
    "          type: string\n" +
    "          example: 'person uid'\n" +
    "          description: 'This property refers to a Person which is the author of the Publication.'\n" +
    "        Contributor:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'Contributor uid'\n" +
    "          description: 'This property refers to a Person which is a contributor of the Publication.'\n" +
    "        licenseUrl:\n" +
    "          type: string\n" +
    "          example: 'https://license.com/'\n" +
    "          description: 'This property contains the URL of the license document that applies to the Publication.'\n" +
    "        keywords:\n" +
    "          type: string\n" +
    "          example: 'keyword1, keyword2'\n" +
    "          description: 'This property contains the keywords used to describe the Publication. Multiple entries in a keywords list are typically delimited by commas.'\n" +
    "        issn:\n" +
    "          type: string\n" +
    "          example: 'issn'\n" +
    "          description: 'This property contains the International Standard Serial Number (ISSN) that identifies the Publication.'\n" +
    "        pages:\n" +
    "          type: string\n" +
    "          example: '100'\n" +
    "          description: 'This property contains the number of pages of the Publication.'\n" +
    "        volumesNumber:\n" +
    "          type: string\n" +
    "          example: '23'\n" +
    "          description: 'This property contains the volume identifier of the Publication.'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Publication. A Publication may be associated with multiple categories.'\n" +
    "    service:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - identifier\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Service.'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Service. A Service may be associated with multiple categories.'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact Point'\n" +
    "          description: 'This property refers to the Contact Point (i.e. Role) defined for the Service.'\n" +
    "        description:\n" +
    "          type: string\n" +
    "          example: 'description'\n" +
    "          description: 'This property contains the description of the Service.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/identifier'\n" +
    "          description: 'This property contains an identifier for the Service.'\n" +
    "        keywords:\n" +
    "          type: string\n" +
    "          example: 'keyword1, keyword2'\n" +
    "          description: 'This property contains the keywords used to describe the Service. Multiple entries in a keywords list are typically delimited by commas.'\n" +
    "        name:\n" +
    "          type: string\n" +
    "          example: 'name'\n" +
    "          description: 'This property contains the name of the Service.'\n" +
    "        pageURL:\n" +
    "          type: string\n" +
    "          example: 'pageURL'\n" +
    "          description: 'This property refers to a page or document about the Service'\n" +
    "        provider:\n" +
    "          type: string\n" +
    "          example: 'provider uid'\n" +
    "          description: 'This property refers to an Organisation or Person responsible for making the Service available.'\n" +
    "        spatialExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/location'\n" +
    "          description: 'This property refers to a geographical location of the Service.'\n" +
    "        temporalExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/periodOfTime'\n" +
    "          description: 'This property refers to a temporal period (i.e. startDate, endDate) that the Service covers.'\n" +
    "        type:\n" +
    "          type: string\n" +
    "          example: 'type'\n" +
    "          description: 'This property refers to the type of the Service.'\n" +
    "    softwareApplication:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - identifier\n" +
    "        - type\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: 'UID'\n" +
    "          description: 'This property contains the main identifier for the Software Application.'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Software Application. A Software Application may be associated with multiple categories.'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact Point'\n" +
    "          description: 'This property refers to the Contact Point (i.e. Role) defined for the Software Application.'\n" +
    "        description:\n" +
    "          type: string\n" +
    "          example: 'description'\n" +
    "          description: 'This property contains the description of the Software Application'\n" +
    "        downloadURL:\n" +
    "          type: string\n" +
    "          example: 'downloadURL'\n" +
    "          description: 'If the Software Application can be downloaded this property contains the URL to download it.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'identifier'\n" +
    "          description: 'This property contains an identifier for the Software Application.'\n" +
    "        installURL:\n" +
    "          type: string\n" +
    "          example: 'installURL'\n" +
    "          description: 'This property contains the URL at which the application may be installed.'\n" +
    "        keywords:\n" +
    "          type: string\n" +
    "          example: 'keyword1, keyword2'\n" +
    "          description: 'This property contains the keywords used to describe the Software Application. Multiple entries in a keywords list are typically delimited by commas.'\n" +
    "        licenseURL:\n" +
    "          type: string\n" +
    "          example: 'licenseURL'\n" +
    "          description: 'This property contains the URL of the license document that applies to the Software Application.'\n" +
    "        mainEntityofPage:\n" +
    "          type: string\n" +
    "          example: 'mainEntityofPage'\n" +
    "          description: 'This property refers to the web page URL which describes the Software Application.'\n" +
    "        name:\n" +
    "          type: string\n" +
    "          example: 'name'\n" +
    "          description: 'This property contains the name of the Software Application'\n" +
    "        parameter:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/parameter'\n" +
    "          description: 'This property represents an input or output parameters of the Software Application.'\n" +
    "        relation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'epos resource'\n" +
    "          description: 'It represents the link to another Epos resource. e.g. Software, WebService, Operation linked to this software.'\n" +
    "        requirements:\n" +
    "          type: string\n" +
    "          example: 'requirements'\n" +
    "          description: 'Component dependency requirements for application. This includes runtime environments and shared libraries that are not included in the application distribution package, but required to run the application.'\n" +
    "        softwareVersion:\n" +
    "          type: string\n" +
    "          example: 'softwareVersion'\n" +
    "          description: 'This property contains the version of the Software Application instance.'\n" +
    "    softwareSourceCode:\n" +
    "      required:\n" +
    "        - identifier\n" +
    "        - type\n" +
    "      properties:\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Software Source Code. A Software Source Code may be associated with multiple categories.'\n" +
    "        codeRepository:\n" +
    "          type: string\n" +
    "          example: 'codeRepository'\n" +
    "          description: 'This property contains the link to the repository where the un-compiled, human readable code and related code is located (SVN, GitHub, CodePlex)'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact Point'\n" +
    "          description: 'This property refers to the Contact Point (i.e. Role) defined for the Software.'\n" +
    "        description:\n" +
    "          type: string\n" +
    "          example: 'description'\n" +
    "          description: 'This property contains the description of the Software'\n" +
    "        downloadURL:\n" +
    "          type: string\n" +
    "          example: 'downloadURL'\n" +
    "          description: 'If the Software can be downloaded this property contains the URL to download it.'\n" +
    "        identifier:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'identifier'\n" +
    "          description: 'This property contains an identifier for the Software.'\n" +
    "        keywords:\n" +
    "          type: string\n" +
    "          example: 'keyword1, keyword2'\n" +
    "          description: 'This property contains the keywords used to describe the Software. Multiple entries in a keywords list are typically delimited by commas.'\n" +
    "        licenseURL:\n" +
    "          type: string\n" +
    "          example: 'licenseURL'\n" +
    "          description: 'This property contains the URL of the license document that applies to the Software.'\n" +
    "        mainEntityOfPage:\n" +
    "          type: string\n" +
    "          example: 'mainEntityOfPage'\n" +
    "          description: 'This property refers to the web page URL which describes the Software.'\n" +
    "        name:\n" +
    "          type: string\n" +
    "          example: 'name'\n" +
    "          description: 'This property contains the name of the Software'\n" +
    "        programmingLanguage:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'programmingLanguage'\n" +
    "          description: 'This property contains the computer programming language used to develop the Software.'\n" +
    "        relation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'epos resource'\n" +
    "          description: 'It represents the link to another Epos resource. e.g. Software, WebService, Operation linked to this software.'\n" +
    "        runtimePlatform:\n" +
    "          type: string\n" +
    "          example: 'runtimePlatform'\n" +
    "          description: 'This property refers to the runtime platform or script interpreter dependencies (Example - Java v1, Python2.3, .Net Framework 3.0).'\n" +
    "        softwareVersion:\n" +
    "          type: string\n" +
    "          example: 'softwareVersion'\n" +
    "          description: 'This property contains the version of the Software instance.'\n" +
    "    webservice:\n" +
    "      required:\n" +
    "        - UID\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: uid\n" +
    "          description: 'This property contains the main identifier for the Web Service.'\n" +
    "        category:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'category'\n" +
    "          description: 'This property refers to a category of the Web Service. A Web Service may be associated with multiple categories.'\n" +
    "        contactPoint:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'contact point'\n" +
    "          description: 'This property contains contact information (i.e. Role) that can be used for sending comments about the Web Service.'\n" +
    "        dataModified:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the most recent date on which the Web Service was modified.'\n" +
    "        dataPublished:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the date of publication of the Web Service.'\n" +
    "        description:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'description'\n" +
    "          description: 'This property contains a free-text description of the Web Service.'\n" +
    "        documentation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/documentation'\n" +
    "          description: 'This property refers to the API documentation.'\n" +
    "        entryPoint:\n" +
    "          type: string\n" +
    "          example: 'Entry Point'\n" +
    "          description: 'This property refers to the API definitions (e.g., WSDL, WADL)'\n" +
    "        keywords:\n" +
    "          type: string\n" +
    "          example: 'keyword1, keyword2'\n" +
    "          description: 'This property contains the keywords used to describe the Web Service. Multiple entries in a keywords list are typically delimited by commas.'\n" +
    "        license:\n" +
    "          type: string\n" +
    "          example: 'license'\n" +
    "          description: 'This property refers to the licence under which the Web Service can be used or reused.'\n" +
    "        name:\n" +
    "          type: string\n" +
    "          example: 'name'\n" +
    "          description: 'This property contains a name given to the Web Service.'\n" +
    "        provider:\n" +
    "          type: string\n" +
    "          example: 'organization'\n" +
    "          description: 'This property refers to an Organisation responsible for making the Web Service available.'\n" +
    "        spatialExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/location'\n" +
    "          description: 'This property refers to a geographical area covered by the Web Service.'\n" +
    "        supportedOperation:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'operation'\n" +
    "          description: 'This property refers to a web service operation supported by the Web Service.'\n" +
    "        temporalExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/periodOfTime'\n" +
    "          description: 'This property refers to a temporal period (i.e. startDate, endDate) that the Web Service covers.'\n" +
    "    operation:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - template\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: uid\n" +
    "          description: 'This property contains the main identifier for the Operation'\n" +
    "        method:\n" +
    "          type: string\n" +
    "          example: get\n" +
    "          description: 'The HTTP method.'\n" +
    "        returns:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'json'\n" +
    "          description: 'This property is used to specify the output format of the Operation. The possible values are listed here: https://www.iana.org/assignments/media-types/media-types.xhtml'\n" +
    "        template:\n" +
    "          type: string\n" +
    "          example: 'uri template'\n" +
    "          description: 'The syntax of the template literal is specified by the [RFC6570] URI Template syntax.'\n" +
    "        spatialExtent:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            $ref: '#/components/schemas/mapping'\n" +
    "          description: 'A variable-to-property mapping of the IRI template.'\n" +
    "    distribution:\n" +
    "      required:\n" +
    "        - UID\n" +
    "        - accessURL\n" +
    "      type: object\n" +
    "      properties:\n" +
    "        UID:\n" +
    "          type: string\n" +
    "          example: uid\n" +
    "          description: 'This property contains the main identifier for the Distribution.'\n" +
    "        accessService:\n" +
    "          type: string\n" +
    "          example: type\n" +
    "          description: 'This property refers to the WebService which supports selection of an extract, sub-set, or combination of data'\n" +
    "        accessURL:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'url'\n" +
    "          description: 'This property contains a URL that gives access to a Distribution of the Data Product. The resource at the access URL may contain information about how to get the Data Product.'\n" +
    "        description:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'description'\n" +
    "          description: 'This property contains a free-text account of the Distribution. This property can be repeated for parallel language versions of the description.'\n" +
    "        downloadURL:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'url'\n" +
    "          description: 'This property contains a URL that is a direct link to a downloadable file in a given format.'\n" +
    "        format:\n" +
    "          type: string\n" +
    "          example: format\n" +
    "          description: 'This property refers to the type of the Distribution. A controlled vocabulary for the values has not been established.'\n" +
    "        issued:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the date of formal issuance (e.g., publication) of the Distribution.'\n" +
    "        licence:\n" +
    "          type: string\n" +
    "          example: licence\n" +
    "          description: 'This property refers to the licence under which the Distribution is made available.'\n" +
    "        modified:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "          description: 'This property contains the most recent date on which the Distribution was changed or modified.'\n" +
    "        title:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'title'\n" +
    "          description: 'This property contains a name given to the Distribution. This property can be repeated for parallel language versions of the description.'\n" +
    "        type:\n" +
    "          type: string\n" +
    "          example: type\n" +
    "          description: 'This property refers to the type of the Distribution.'\n" +
    "    mapping:\n" +
    "      required:\n" +
    "        - variable\n" +
    "        - label\n" +
    "        - required\n" +
    "        - range\n" +
    "      properties:\n" +
    "        defaultValue:\n" +
    "          type: string\n" +
    "          example: 'default Value'\n" +
    "          description: 'This property contains the default value of the parameter.'\n" +
    "        label:\n" +
    "          type: string\n" +
    "          example: 'label'\n" +
    "          description: 'This property contains a short string used to describe the meaning of the parameter.'\n" +
    "        maxValue:\n" +
    "          type: string\n" +
    "          example: 'max Value'\n" +
    "          description: 'This property contains the maximum value of the parameter.'\n" +
    "        minValue:\n" +
    "          type: string\n" +
    "          example: 'min Value'\n" +
    "          description: 'This property contains the minimum value of the parameter.'\n" +
    "        paramValue:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'Param Value'\n" +
    "          description: 'This property contains one of the possible values which should be used in the web service query. It could be repeated as many times as needed.'\n" +
    "        property:\n" +
    "          type: string\n" +
    "          example: 'property'\n" +
    "          description: 'This property contains the vocabulary term which indicates the semantic description of parameter.'\n" +
    "        range:\n" +
    "          type: string\n" +
    "          example: 'range'\n" +
    "          description: 'This property contains the type of parameter'\n" +
    "        required:\n" +
    "          type: string\n" +
    "          example: 'required'\n" +
    "          description: 'This property contains true if the property is required, false otherwise.'\n" +
    "        valuePattern:\n" +
    "          type: string\n" +
    "          example: 'value Pattern'\n" +
    "          description: 'This property contains the regular expression for testing values according to the parameters specification.'\n" +
    "        variable:\n" +
    "          type: string\n" +
    "          example: 'variable'\n" +
    "          description: 'This property contains the name of the parameter as required by web service specifications.'\n" +
    "    location:\n" +
    "      type: object\n" +
    "      properties:\n" +
    "        latitude:\n" +
    "          type: string\n" +
    "          example: 'coordinate latitude'\n" +
    "        longitude:\n" +
    "          type: string\n" +
    "          example: 'coordinate longitude'\n" +
    "        geobbox:\n" +
    "          type: array\n" +
    "          items:\n" +
    "            type: string\n" +
    "            example: 'geobbox'\n" +
    "    periodOfTime:\n" +
    "      type: object\n" +
    "      properties:\n" +
    "        startDate:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "        endDate:\n" +
    "          type: string\n" +
    "          format: date-time\n" +
    "    documentation:\n" +
    "      type: object\n" +
    "      properties:\n" +
    "        title:\n" +
    "          type: string\n" +
    "          example: 'title'\n" +
    "        description:\n" +
    "          type: string\n" +
    "          example: 'description'\n" +
    "        uri:\n" +
    "          type: string\n" +
    "          example: 'url'\n" +
    "    identifier:\n" +
    "      type: object\n" +
    "      required:\n" +
    "        - identifier\n" +
    "        - type\n" +
    "      properties:\n" +
    "        type:\n" +
    "          type: string\n" +
    "          example: 'DDSS-ID'\n" +
    "          description: 'This property refers the identifier scheme referenced by its datatype (e.g., DDSS-ID, DOI, etc.).'\n" +
    "        identifier:\n" +
    "          type: string\n" +
    "          example: 'id'\n" +
    "          description: 'This property contains a string that is an identifier in the context of the identifier scheme referenced by its datatype'\n" +
    "    address:\n" +
    "      type: object\n" +
    "      required:\n" +
    "        - country\n" +
    "        - locality\n" +
    "        - postalCode\n" +
    "        - street\n" +
    "      properties:\n" +
    "        country:\n" +
    "          type: string\n" +
    "          example: 'country'\n" +
    "          description: 'The country. For example, USA. You can also provide the two-letter ISO 3166-1 alpha-2 country code.'\n" +
    "        locality:\n" +
    "          type: string\n" +
    "          example: 'locality'\n" +
    "          description: 'The locality in which the street address is, and which is in the region. For example, Mountain View.'\n" +
    "        postalCode:\n" +
    "          type: string\n" +
    "          example: 'postalCode'\n" +
    "          description: 'The postal code. For example, 94043.'\n" +
    "        street:\n" +
    "          type: string\n" +
    "          example: 'street'\n" +
    "          description: 'The street address. For example, 1600 Amphitheatre Pkwy.'\n" +
    "    parameter:\n" +
    "      type: object\n" +
    "      required:\n" +
    "        - action\n" +
    "      properties:\n" +
    "        encodingFormat:\n" +
    "          type: string\n" +
    "          example: 'application/json'\n" +
    "          description: 'Format'\n" +
    "        conformsTo:\n" +
    "          type: string\n" +
    "          example: 'https://site.com/schema/request.json'\n" +
    "          description: 'schema of the request'\n" +
    "        action:\n" +
    "          type: string\n" +
    "          example: 'object or result'\n" +
    "          enum:\n" +
    "            - object\n" +
    "            - result\n" +
    "          description: 'The type of action, can be object or result'\n" +
    "  securitySchemes:\n" +
    "    aai:\n" +
    "      type: apiKey\n" +
    "      in: header\n" +
    "      name: Authorization\n"

fs.writeFileSync('./static/swagger.yaml', start)
