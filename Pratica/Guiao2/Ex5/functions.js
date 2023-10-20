prefix = function prefix() {
    var prefixes = db.phones.aggregate([
        {$group: {_id: "$components.prefix", totalPrefixes: {$sum: 1}}},
        {$project: {_id: 0, "prefix": "$_id", totalPrefixes: 1 }}
    ]);

    print(prefixes);
}   

patterns = function patterns() {
    var numbers = db.phones.find({}, { "_id": 1 }).toArray();

    var elements = [];
    var el_no_rep = [];

    numbers.forEach(function (number) {
        elements.push(number._id);
    });

    for (var el in elements) {
        const numStr = elements[el].toString();
        const reversedStr = numStr.split('').reverse().join('');

        if (numStr === reversedStr) {
            el_no_rep.push(elements[el]);
        }
    }
    print(el_no_rep)
}
