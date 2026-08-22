export type DuplicatesDto = {
    duplicates: DuplicateDto[]
};

export type DuplicateDto = {
    id: number;
    filename: string;
    absolutePath: string;
    fileSize: number;
    sha256: string;
    perceptualHash: number;
    dateTaken: string;
    width: number;
    height: number;
    exactMatch?: boolean;
};

export const mockDuplicatesData: DuplicatesDto[] = [
  {
    "duplicates": [
      {
        "id": 1440,
        "filename": "IMG_0674.JPG",
        "absolutePath": "f:\\photos\\fromCanon\\2006_04_09_xalkida\\.picasaoriginals\\IMG_0674.JPG",
        "fileSize": 682579,
        "sha256": "317d05e8ee3f068eeecc1adf8d2cfaddad995b04d898a1d2636cc66ac6088b62",
        "perceptualHash": 6660170428257823000,
        "dateTaken": "2006-04-09",
        "width": 2048,
        "height": 1536,
      },
      {
        "id": 1443,
        "filename": "IMG_0674.JPG",
        "absolutePath": "f:\\photos\\fromCanon\\2006_04_09_xalkida\\IMG_0674.JPG",
        "fileSize": 906782,
        "sha256": "6d804d28535adb05663d3ee83a8c670ce11f13316ac3fd7446d8d96d9930d2ab",
        "perceptualHash": 6660170428257823000,
        "dateTaken": "2006-04-09",
        "width": 2048,
        "height": 1536,
        "exactMatch": false
      }
    ]
  },
  {
    "duplicates": [
      {
        "id": 4799,
        "filename": "IMG_4610_2.JPG",
        "absolutePath": "f:\\photos\\fromCanon\\2009_03_15\\IMG_4610_2.JPG",
        "fileSize": 452348,
        "sha256": "6058e76e772a33c675892d147b56359b3c2e436e1fd2d275e9d0d59ceb39531b",
        "perceptualHash": 3004086737392940000,
        "dateTaken": "2009-03-15",
        "width": 2592,
        "height": 1944,
      },
      {
        "id": 4839,
        "filename": "IMG_4610.JPG",
        "absolutePath": "f:\\photos\\fromCanon\\2009_03_15_maketes_telikes_dpl\\IMG_4610.JPG",
        "fileSize": 452348,
        "sha256": "6058e76e772a33c675892d147b56359b3c2e436e1fd2d275e9d0d59ceb39531b",
        "perceptualHash": 3004086737392940000,
        "dateTaken": "2009-03-15",
        "width": 2592,
        "height": 1944,
        "exactMatch": true
      }
    ]
  },
  {
    "duplicates": [
      {
        "id": 4787,
        "filename": "IMG_4604_2.JPG",
        "absolutePath": "f:\\photos\\fromCanon\\2009_03_15\\IMG_4604_2.JPG",
        "fileSize": 397287,
        "sha256": "442cec9a051e1661ac92b56b57dacfbc24fc72e69aad7a01bbab639516dbe62b",
        "perceptualHash": 6382335427292555000,
        "dateTaken": "2009-03-15",
        "width": 2592,
        "height": 1944,
      },
      {
        "id": 4833,
        "filename": "IMG_4604.JPG",
        "absolutePath": "f:\\photos\\fromCanon\\2009_03_15_maketes_telikes_dpl\\IMG_4604.JPG",
        "fileSize": 397287,
        "sha256": "442cec9a051e1661ac92b56b57dacfbc24fc72e69aad7a01bbab639516dbe62b",
        "perceptualHash": 6382335427292555000,
        "dateTaken": "2009-03-15",
        "width": 2592,
        "height": 1944,
        "exactMatch": true
      }
    ]
  }
];