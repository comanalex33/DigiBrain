
const utils = {

    getFormattedTimeFromTimestamp: (timestamp) => {
        const dateTime = new Date(timestamp);

        const options = {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        };

        const formattedDateTime = dateTime.toLocaleString('en-US', options);
        return formattedDateTime
    }
}

export default utils;