# link-shorten

A very simple link shortener written in Clojure with Postgres. This was done for learning purposes
only. It does not create the shortest URL possible. Hopefully this project
will help those out figuring out how to deploy to Heroku and making korma
work with Heroku's DATABASE_URL



## Usage

You can start the server by running

    $ lein run

Install the [heroku toolbelt](https://toolbelt.herokuapp.com)
and run


    $ heroku apps:create link-shorten
    Creating link-shorten... done, stack is cedar
    http://link-shorten.herokuapp.com/ | git@heroku.com:link-shorten.git
    Git remote heroku added

Add the domain that Heroku gives you as an environment variable. Do not include the trailing slash
    $ heroku config:add domain=http://link-shorten.herokuapp.com

Add the postgres addon

    $ heroku addons:add heroku-postgresql

You can then deploy with

    $ git push heroku master

## Remote REPL

You can connect to the deployed project via the REPL to debug

    $ heroku config:add user=[...] password=[...]

Then you can launch the REPL:

    $ lein repl :connect http://$REPL_USER:$REPL_PASSWORD@link-shorten.herokuapp.com:80/u/repl

## License

Copyright © 2014 Brian Sunter

Distributed under the Eclipse Public License, the same as Clojure.
